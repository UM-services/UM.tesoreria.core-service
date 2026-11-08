package um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout.ClickPagosLayout;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout.DebitoDirectoLayout;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout.DebitoVisaLayout;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout.LayoutFormat;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.application.layout.PmcLayout;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CanalGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.CuotaDeuda;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.DebitoPendiente;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.InconsistenciaGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.LineaGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.PreviewGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.ResultadoOperacionDebito;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudAnulacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudGeneracion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.model.SolicitudReprogramacion;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.CuotaDeudaRepository;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.DebitoGeneracionRepository;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.PagarFileGenerator;
import um.tesoreria.core.hexagonal.cobranza.generacionArchivo.domain.ports.out.PayPerTicFileGenerator;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Generación de archivos de facturación y débito.
 *
 * <p>Composición cross-slice autorizada: usa los <em>application services</em> de
 * {@code chequeraCuota}, {@code chequeraSerie} y {@code chequeraPago} para resolver cuotas,
 * personas y pagos. PAGAR y PayPerTIC se delegan a los generadores existentes hasta completar su
 * traslado al slice.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class GeneracionArchivoService {

    private static final int DEBITO_TIPO_VISA = 1;
    private static final int DEBITO_TIPO_DIRECTO = 2;

    private final CuotaDeudaRepository cuotaDeudaRepository;
    private final DebitoGeneracionRepository debitoGeneracionRepository;
    private final PagarFileGenerator pagarFileGenerator;
    private final PayPerTicFileGenerator payPerTicFileGenerator;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraPagoService chequeraPagoService;

    public PreviewGeneracion preview(SolicitudGeneracion solicitud) {
        validarPeriodo(solicitud);
        return switch (solicitud.canal()) {
            case PMC -> previewCuotas(solicitud, cuotasPmc(solicitud));
            case CLICK_PAGOS -> previewCuotas(solicitud, cuotaDeudaRepository.findAllMacroByRango(
                    solicitud.desde(), solicitud.hasta()));
            case PAGAR -> previewCuotas(solicitud, cuotaDeudaRepository.findAllByRango(
                    solicitud.desde(), solicitud.hasta()));
            case PAY_PER_TIC -> previewCuotas(solicitud, cuotaDeudaRepository.findPptByRango(
                    solicitud.desde(), solicitud.hasta()));
            case DEBITO_DIRECTO -> previewDebitoDirecto(solicitud);
            case DEBITO_VISA -> previewVisa(solicitud);
        };
    }

    @Transactional
    public ResultadoGeneracion generar(SolicitudGeneracion solicitud) {
        validarPeriodo(solicitud);
        return switch (solicitud.canal()) {
            case PMC -> generarCuotas(solicitud, cuotasPmc(solicitud), true);
            case CLICK_PAGOS -> generarCuotas(solicitud, cuotaDeudaRepository.findAllMacroByRango(
                    solicitud.desde(), solicitud.hasta()), false);
            case DEBITO_DIRECTO -> generarDebitoDirecto(solicitud);
            case DEBITO_VISA -> generarVisa(solicitud);
            case PAGAR -> completarConteos(pagarFileGenerator.generar(solicitud.desde(), solicitud.hasta()),
                    preview(solicitud));
            case PAY_PER_TIC -> completarConteos(payPerTicFileGenerator.generar(solicitud.desde(), solicitud.hasta()),
                    preview(solicitud));
        };
    }

    @Transactional
    public ResultadoOperacionDebito anular(SolicitudAnulacion solicitud) {
        if (solicitud.canal() == null || !solicitud.canal().isDebito()) {
            throw new IllegalArgumentException("La anulación de envío sólo aplica a canales de débito");
        }
        if (solicitud.fechaEnvio() == null) {
            throw new IllegalArgumentException("La fecha de envío es obligatoria");
        }
        Integer debitoTipoId = solicitud.canal() == CanalGeneracion.DEBITO_VISA
                ? DEBITO_TIPO_VISA : DEBITO_TIPO_DIRECTO;
        boolean soloSantander = solicitud.canal() == CanalGeneracion.DEBITO_DIRECTO
                && solicitud.soloSantander();
        boolean soloOtrosBancos = solicitud.canal() == CanalGeneracion.DEBITO_DIRECTO
                && solicitud.soloOtrosBancos();

        int anulados = 0;
        int omitidos = 0;
        for (DebitoPendiente debito : debitoGeneracionRepository.findByFechaEnvio(
                solicitud.fechaEnvio(), soloSantander, soloOtrosBancos, debitoTipoId)) {
            if (tienePago(debito)) {
                omitidos++;
                continue;
            }
            debitoGeneracionRepository.marcarFechaEnvio(debito.getDebitoId(), null);
            anulados++;
        }
        return new ResultadoOperacionDebito("ANULACION", anulados, omitidos);
    }

    @Transactional
    public ResultadoOperacionDebito reprogramar(SolicitudReprogramacion solicitud) {
        if (solicitud.desde() == null || solicitud.hasta() == null) {
            throw new IllegalArgumentException("El período desde/hasta es obligatorio");
        }
        if (solicitud.fechaVencimiento() == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es obligatoria");
        }
        int actualizados = 0;
        int omitidos = 0;
        for (DebitoPendiente debito : debitoGeneracionRepository.findPendientes(
                solicitud.desde(), solicitud.hasta(), solicitud.soloSantander(),
                solicitud.soloOtrosBancos(), DEBITO_TIPO_DIRECTO)) {
            ChequeraCuota cuota = buscarCuota(debito);
            if (cuota == null || tienePago(debito) || cuota.getVencimiento1() == null
                    || cuota.getVencimiento1().isAfter(solicitud.fechaVencimiento())) {
                omitidos++;
                continue;
            }
            OffsetDateTime nuevaFecha = solicitud.fechaVencimiento();
            cuota.setVencimiento1(nuevaFecha);
            if (cuota.getVencimiento2() == null || cuota.getVencimiento2().isBefore(nuevaFecha.plusDays(1))) {
                cuota.setVencimiento2(nuevaFecha.plusDays(1));
            }
            if (cuota.getVencimiento3() == null
                    || cuota.getVencimiento3().isBefore(cuota.getVencimiento2().plusDays(1))) {
                cuota.setVencimiento3(cuota.getVencimiento2().plusDays(1));
            }
            chequeraCuotaService.update(cuota, cuota.getChequeraCuotaId());
            debitoGeneracionRepository.findAllAsociados(debito.getFacultadId(), debito.getTipoChequeraId(),
                            debito.getChequeraSerieId(), debito.getProductoId(), debito.getAlternativaId(),
                            debito.getCuotaId())
                    .forEach(asociado -> debitoGeneracionRepository.actualizarFechaVencimiento(
                            asociado.getDebitoId(), nuevaFecha));
            actualizados++;
        }
        return new ResultadoOperacionDebito("REPROGRAMACION", actualizados, omitidos);
    }

    private List<CuotaDeuda> cuotasPmc(SolicitudGeneracion solicitud) {
        if (solicitud.grupo() != null) {
            return cuotaDeudaRepository.findAllByGrupoRango(solicitud.grupo(),
                    solicitud.desde(), solicitud.hasta());
        }
        return cuotaDeudaRepository.findAllByRango(solicitud.desde(), solicitud.hasta());
    }

    private PreviewGeneracion previewCuotas(SolicitudGeneracion solicitud, List<CuotaDeuda> cuotas) {
        List<LineaGeneracion> lineas = cuotas.stream().map(this::linea).toList();
        List<InconsistenciaGeneracion> inconsistencias = new ArrayList<>();
        cuotas.forEach(cuota -> inconsistencias.addAll(inconsistencias(cuota)));
        return new PreviewGeneracion(solicitud.canal(), lineas.size(), total(cuotas), 0,
                inconsistencias, lineas);
    }

    private PreviewGeneracion previewDebitoDirecto(SolicitudGeneracion solicitud) {
        List<LineaGeneracion> lineas = new ArrayList<>();
        List<InconsistenciaGeneracion> inconsistencias = new ArrayList<>();
        int excluidos = 0;
        for (DebitoPendiente debito : debitoGeneracionRepository.findPendientes(
                solicitud.desde(), solicitud.hasta(), solicitud.soloSantander(),
                solicitud.soloOtrosBancos(), DEBITO_TIPO_DIRECTO)) {
            ChequeraCuota cuota = buscarCuota(debito);
            if (cuota == null) {
                inconsistencias.add(new InconsistenciaGeneracion(null, debito.getDebitoId(),
                        "No existe la cuota del débito"));
                excluidos++;
                continue;
            }
            if (cuota.getBaja() != null && cuota.getBaja() != 0) {
                excluidos++;
                continue;
            }
            if (cuota.getImporte1() == null || cuota.getImporte1().compareTo(BigDecimal.ZERO) <= 0
                    || tienePago(debito)) {
                excluidos++;
                continue;
            }
            lineas.add(linea(debito, cuota));
        }
        return new PreviewGeneracion(solicitud.canal(), lineas.size(), totalLineas(lineas),
                excluidos, inconsistencias, lineas);
    }

    private PreviewGeneracion previewVisa(SolicitudGeneracion solicitud) {
        List<LineaGeneracion> lineas = new ArrayList<>();
        List<InconsistenciaGeneracion> inconsistencias = new ArrayList<>();
        int excluidos = 0;
        for (DebitoPendiente debito : debitoGeneracionRepository.findNoEnviadosAltas(
                solicitud.desde(), solicitud.hasta(), DEBITO_TIPO_VISA)) {
            ChequeraCuota cuota = buscarCuota(debito);
            if (cuota == null) {
                inconsistencias.add(new InconsistenciaGeneracion(null, debito.getDebitoId(),
                        "No existe la cuota del débito"));
                excluidos++;
                continue;
            }
            if (cuota.getBaja() != null && cuota.getBaja() != 0) {
                excluidos++;
                continue;
            }
            lineas.add(linea(debito, cuota));
        }
        return new PreviewGeneracion(solicitud.canal(), lineas.size(), totalLineas(lineas),
                excluidos, inconsistencias, lineas);
    }

    private ResultadoGeneracion generarCuotas(SolicitudGeneracion solicitud, List<CuotaDeuda> cuotas,
                                              boolean pmc) {
        List<String> lineas = new ArrayList<>();
        OffsetDateTime hoy = hoy();
        BigDecimal total = total(cuotas);
        lineas.add(pmc ? PmcLayout.header(hoy) : ClickPagosLayout.header(hoy));
        cuotas.forEach(cuota -> lineas.add(pmc ? PmcLayout.detalle(cuota) : ClickPagosLayout.detalle(cuota)));
        lineas.add(pmc ? PmcLayout.trailer(hoy, cuotas.size(), total)
                : ClickPagosLayout.trailer(hoy, cuotas.size(), total));
        String filename = (pmc ? "PMC_" : "ClickDePagos_") + hoy.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")) + ".txt";
        return new ResultadoGeneracion(filename, "text/plain", bytes(lineas), cuotas.size(), total);
    }

    private ResultadoGeneracion generarDebitoDirecto(SolicitudGeneracion solicitud) {
        OffsetDateTime hoy = hoy();
        String hora = LayoutFormat.hora(OffsetDateTime.now(ZoneOffset.UTC), "HHmmss");
        List<String> lineas = new ArrayList<>();
        int registros = 0;
        long totalCentavos = 0;
        for (DebitoPendiente debito : debitoGeneracionRepository.findPendientes(
                solicitud.desde(), solicitud.hasta(), solicitud.soloSantander(),
                solicitud.soloOtrosBancos(), DEBITO_TIPO_DIRECTO)) {
            ChequeraCuota cuota = buscarCuota(debito);
            if (cuota == null || (cuota.getBaja() != null && cuota.getBaja() != 0)
                    || cuota.getImporte1() == null || cuota.getImporte1().compareTo(BigDecimal.ZERO) <= 0
                    || tienePago(debito)) {
                continue;
            }
            String cbu = DebitoDirectoLayout.normalizarCbu(debito.getCbu());
            if (!Objects.equals(cbu, debito.getCbu())) {
                debitoGeneracionRepository.actualizarCbu(debito.getDebitoId(), cbu);
                debito.setCbu(cbu);
            }
            ChequeraSerie serie = buscarSerie(debito);
            String documentoId = serie == null || serie.getDocumentoId() == null
                    ? "" : String.valueOf(serie.getDocumentoId());
            String personaId = serie == null || serie.getPersonaId() == null
                    ? "" : serie.getPersonaId().toBigInteger().toString();
            lineas.add(DebitoDirectoLayout.detalle(debito, cuota.getImporte1(), documentoId, personaId, hoy, hora));
            registros++;
            totalCentavos += LayoutFormat.centavos(cuota.getImporte1());
            debitoGeneracionRepository.marcarFechaEnvio(debito.getDebitoId(), hoy);
        }
        lineas.add(DebitoDirectoLayout.control(hoy, hora, registros, totalCentavos));
        String filename = DebitoDirectoLayout.filename(solicitud.soloSantander(),
                solicitud.soloOtrosBancos(), hoy);
        return new ResultadoGeneracion(filename, "text/plain", bytes(lineas), registros,
                BigDecimal.valueOf(totalCentavos).movePointLeft(2));
    }

    private ResultadoGeneracion generarVisa(SolicitudGeneracion solicitud) {
        OffsetDateTime hoy = hoy();
        String hora = LayoutFormat.hora(OffsetDateTime.now(ZoneOffset.UTC), "HHmmss");
        List<String> lineas = new ArrayList<>();
        int registros = 0;
        long totalImporte = 0;
        lineas.add(DebitoVisaLayout.header(hoy, hora));
        for (DebitoPendiente debito : debitoGeneracionRepository.findNoEnviadosAltas(
                solicitud.desde(), solicitud.hasta(), DEBITO_TIPO_VISA)) {
            ChequeraCuota cuota = buscarCuota(debito);
            if (cuota == null || (cuota.getBaja() != null && cuota.getBaja() != 0)) {
                continue;
            }
            ChequeraSerie serie = buscarSerie(debito);
            Integer claseChequeraId = serie == null || serie.getTipoChequera() == null
                    ? null : serie.getTipoChequera().getClaseChequeraId();
            boolean primerEnvio = !debitoGeneracionRepository.existsEnviadoByCuota(
                    debito.getFacultadId(), debito.getTipoChequeraId(), debito.getChequeraSerieId(),
                    debito.getProductoId(), debito.getAlternativaId(), debito.getCuotaId(), DEBITO_TIPO_VISA);
            lineas.add(DebitoVisaLayout.detalle(debito, cuota.getImporte1(), claseChequeraId,
                    solicitud.descuentoConDefault(), primerEnvio));
            registros++;
            totalImporte += DebitoVisaLayout.importeConDescuento(cuota.getImporte1(), claseChequeraId,
                    solicitud.descuentoConDefault());
            debitoGeneracionRepository.marcarFechaEnvio(debito.getDebitoId(), hoy);
        }
        lineas.add(DebitoVisaLayout.trailer(hoy, hora, registros, totalImporte));
        return new ResultadoGeneracion("DEBLIQC.txt", "text/plain", bytes(lineas), registros,
                BigDecimal.valueOf(totalImporte));
    }

    private ChequeraCuota buscarCuota(DebitoPendiente debito) {
        try {
            return chequeraCuotaService.findByUnique(debito.getFacultadId(), debito.getTipoChequeraId(),
                    debito.getChequeraSerieId(), debito.getProductoId(), debito.getAlternativaId(),
                    debito.getCuotaId());
        } catch (RuntimeException e) {
            return null;
        }
    }

    private ChequeraSerie buscarSerie(DebitoPendiente debito) {
        try {
            return chequeraSerieService.findByUnique(debito.getFacultadId(), debito.getTipoChequeraId(),
                    debito.getChequeraSerieId());
        } catch (ChequeraSerieException e) {
            return null;
        }
    }

    private boolean tienePago(DebitoPendiente debito) {
        return chequeraPagoService.isPagado(debito.getFacultadId(), debito.getTipoChequeraId(),
                debito.getChequeraSerieId(), debito.getProductoId(), debito.getAlternativaId(),
                debito.getCuotaId());
    }

    private LineaGeneracion linea(CuotaDeuda cuota) {
        return new LineaGeneracion(cuota.getFacultadId(), cuota.getTipoChequeraId(),
                cuota.getChequeraSerieId(), cuota.getProductoId(), cuota.getAlternativaId(),
                cuota.getCuotaId(), cuota.getMes(), cuota.getAnho(), cuota.getImporte1(),
                cuota.getVencimiento1(), cuota.getVencimiento2(), cuota.getVencimiento3(),
                cuota.getFacultadNombre(), cuota.getTipoChequeraNombre(), cuota.getGeograficaNombre(),
                cuota.getPersonaNombre(), cuota.getProductoNombre(), cuota.getLectivoNombre(),
                null, null, null, null);
    }

    private LineaGeneracion linea(DebitoPendiente debito, ChequeraCuota cuota) {
        ChequeraSerie serie = buscarSerie(debito);
        return new LineaGeneracion(debito.getFacultadId(), debito.getTipoChequeraId(),
                debito.getChequeraSerieId(), debito.getProductoId(), debito.getAlternativaId(),
                debito.getCuotaId(), cuota.getMes(), cuota.getAnho(), cuota.getImporte1(),
                cuota.getVencimiento1(), cuota.getVencimiento2(), cuota.getVencimiento3(),
                serie == null || serie.getFacultad() == null ? null : serie.getFacultad().getNombre(),
                serie == null || serie.getTipoChequera() == null ? null : serie.getTipoChequera().getNombre(),
                serie == null || serie.getGeografica() == null ? null : serie.getGeografica().getNombre(),
                nombrePersona(serie),
                cuota.getProducto() == null ? null : cuota.getProducto().getNombre(),
                serie == null || serie.getLectivo() == null ? null : serie.getLectivo().getNombre(),
                debito.getDebitoId(), debito.getCbu(), debito.getNumeroTarjeta(),
                debito.getFechaVencimiento());
    }

    private String nombrePersona(ChequeraSerie serie) {
        if (serie == null || serie.getPersona() == null) {
            return null;
        }
        return (Objects.toString(serie.getPersona().getApellido(), "") + ", "
                + Objects.toString(serie.getPersona().getNombre(), "")).trim();
    }

    private List<InconsistenciaGeneracion> inconsistencias(CuotaDeuda cuota) {
        List<InconsistenciaGeneracion> resultado = new ArrayList<>();
        if (cuota.getImporte2() != null && cuota.getImporte1() != null
                && cuota.getImporte2().compareTo(cuota.getImporte1()) < 0) {
            resultado.add(new InconsistenciaGeneracion(cuota.getChequeraCuotaId(), null,
                    "2do importe menor que el 1er importe"));
        }
        if (cuota.getImporte3() != null && cuota.getImporte2() != null
                && cuota.getImporte3().compareTo(cuota.getImporte2()) < 0) {
            resultado.add(new InconsistenciaGeneracion(cuota.getChequeraCuotaId(), null,
                    "3er importe menor que el 2do importe"));
        }
        if (cuota.getVencimiento2() != null && cuota.getVencimiento1() != null
                && cuota.getVencimiento2().isBefore(cuota.getVencimiento1())) {
            resultado.add(new InconsistenciaGeneracion(cuota.getChequeraCuotaId(), null,
                    "2do vencimiento menor que el 1er vencimiento"));
        }
        if (cuota.getVencimiento3() != null && cuota.getVencimiento2() != null
                && cuota.getVencimiento3().isBefore(cuota.getVencimiento2())) {
            resultado.add(new InconsistenciaGeneracion(cuota.getChequeraCuotaId(), null,
                    "3er vencimiento menor que el 2do vencimiento"));
        }
        return resultado;
    }

    private BigDecimal total(List<CuotaDeuda> cuotas) {
        return cuotas.stream().map(CuotaDeuda::getImporte1)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal totalLineas(List<LineaGeneracion> lineas) {
        return lineas.stream().map(LineaGeneracion::importe)
                .filter(Objects::nonNull).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private ResultadoGeneracion completarConteos(ResultadoGeneracion resultado, PreviewGeneracion preview) {
        return new ResultadoGeneracion(resultado.filename(), resultado.contentType(), resultado.bytes(),
                preview.registros(), preview.total());
    }

    private byte[] bytes(List<String> lineas) {
        return (String.join("\r\n", lineas) + "\r\n").getBytes(StandardCharsets.ISO_8859_1);
    }

    private OffsetDateTime hoy() {
        return LocalDate.now(ZoneOffset.UTC).atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    private void validarPeriodo(SolicitudGeneracion solicitud) {
        if (solicitud == null || solicitud.canal() == null) {
            throw new IllegalArgumentException("Canal de generación requerido");
        }
        if (solicitud.desde() == null || solicitud.hasta() == null) {
            throw new IllegalArgumentException("El período desde/hasta es obligatorio");
        }
        if (solicitud.desde().isAfter(solicitud.hasta())) {
            throw new IllegalArgumentException("El período es inválido: desde es posterior a hasta");
        }
    }

}
