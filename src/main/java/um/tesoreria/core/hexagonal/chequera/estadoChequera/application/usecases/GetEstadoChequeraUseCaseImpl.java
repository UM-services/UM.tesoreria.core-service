package um.tesoreria.core.hexagonal.chequera.estadoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.exception.ArancelTipoException;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.application.service.ArancelTipoService;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoChequera;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.CuotaEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.DebitoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.ProductoEstado;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.ports.in.GetEstadoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.dependencias.facultad.application.service.FacultadService;
import um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.lectivo.application.exception.LectivoException;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.TipoImpresion;
import um.tesoreria.core.model.dto.ChequeraCuotaPagosDto;
import um.tesoreria.core.model.dto.ChequeraPagoDto;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.DebitoTipoService;
import um.tesoreria.core.service.TipoImpresionService;
import um.tesoreria.core.service.facade.ChequeraService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Arma el {@link EstadoChequera} que consume {@code report-service} para el PDF "Estado de Chequera".
 * <p>
 * Es el armado de datos que antes hacía {@code FormulariosToPdfService.generateEstadoChequeraPdf}
 * en este servicio (el dibujo del PDF pasó a {@code report-service}), con las mismas reglas:
 * <ul>
 *   <li>Los productos salen ordenados por {@code productoId} ascendente (Matrícula antes que Arancel).</li>
 *   <li>Los subtotales de cada producto son los oficiales de {@code chequera_total}; no se recalculan.</li>
 *   <li>El título de la cuota ("Arancel Mensual") y la cantidad de cuotas salen de {@code chequera_alternativa};
 *       si no existe se usa el nombre del producto y la cantidad de cuotas recibidas.</li>
 *   <li>De cada cuota se toma su primer pago ({@code chequera_pago}), que puede no existir.</li>
 *   <li>Los datos del encabezado (facultad, titular, lectivo, tipo de arancel, tipo de impresión) son
 *       opcionales: si falta alguno queda en {@code null}. La chequera y su tipo son obligatorios.</li>
 *   <li>Se traen los débitos de TODOS los tipos de la chequera (VISA + Directo), no uno solo; cada uno
 *       lleva su {@code tipoDebito} (nombre de {@code debito_tipo}) para distinguirlos.</li>
 *   <li>El importe de cada débito se toma de la cuota correspondiente ({@code productoId}, {@code alternativaId},
 *       {@code cuotaId}); {@code Debito} no tiene un importe propio mapeado.</li>
 * </ul>
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class GetEstadoChequeraUseCaseImpl implements GetEstadoChequeraUseCase {

    private final ChequeraSerieService chequeraSerieService;
    private final FacultadService facultadService;
    private final TipoChequeraService tipoChequeraService;
    private final PersonaService personaService;
    private final LectivoService lectivoService;
    private final ArancelTipoService arancelTipoService;
    private final TipoImpresionService tipoImpresionService;
    private final ChequeraService chequeraService;
    private final ChequeraTotalService chequeraTotalService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final DebitoService debitoService;
    private final DebitoTipoService debitoTipoService;

    @Override
    public EstadoChequera getEstadoChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                            Integer alternativaId) {
        log.debug("Processing GetEstadoChequeraUseCaseImpl.getEstadoChequera");
        ChequeraSerie serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        Facultad facultad = null;
        try {
            facultad = facultadService.findByFacultadId(serie.getFacultadId());
        } catch (FacultadException e) {
            log.debug("Facultad {} no encontrada para el estado de chequera", serie.getFacultadId());
        }
        TipoChequera tipoChequera = tipoChequeraService.findByTipoChequeraId(serie.getTipoChequeraId());
        Persona persona = null;
        try {
            persona = personaService.findByUnique(serie.getPersonaId(), serie.getDocumentoId());
        } catch (PersonaException e) {
            log.debug("Persona {} no encontrada para el estado de chequera", serie.getPersonaId());
        }
        Lectivo lectivo = null;
        try {
            lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
        } catch (LectivoException e) {
            log.debug("Lectivo {} no encontrado para el estado de chequera", serie.getLectivoId());
        }
        ArancelTipoEntity arancelTipo = null;
        try {
            arancelTipo = arancelTipoService.findByArancelTipoId(serie.getArancelTipoId());
        } catch (ArancelTipoException e) {
            log.debug("Tipo de arancel {} no encontrado para el estado de chequera", serie.getArancelTipoId());
        }
        TipoImpresion tipoImpresion = null;
        try {
            tipoImpresion = tipoImpresionService.findByTipoImpresionId(serie.getTipoImpresionId());
        } catch (Exception e) {
            log.debug("Tipo de impresión {} no encontrado para el estado de chequera", serie.getTipoImpresionId());
        }

        List<ChequeraCuotaPagosDto> cuotaPagos = chequeraService.findAllCuotaPagosByChequera(facultadId,
                tipoChequeraId, chequeraSerieId, alternativaId);

        // Totales oficiales por producto (chequera_total: total / pagado); no se recalculan.
        Map<Integer, ChequeraTotal> totales = new HashMap<>();
        for (ChequeraTotal total : chequeraTotalService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId)) {
            totales.put(total.getProductoId(), total);
        }

        // TreeMap: orden ascendente de productoId, en la práctica deja Matrícula antes que Arancel.
        Map<Integer, List<ChequeraCuotaPagosDto>> porProducto = new TreeMap<>();
        for (ChequeraCuotaPagosDto cuota : cuotaPagos) {
            porProducto.computeIfAbsent(cuota.getProductoId(), k -> new ArrayList<>()).add(cuota);
        }

        List<ProductoEstado> productos = new ArrayList<>();
        for (Map.Entry<Integer, List<ChequeraCuotaPagosDto>> entry : porProducto.entrySet()) {
            productos.add(toProducto(facultadId, tipoChequeraId, chequeraSerieId, alternativaId,
                    entry.getKey(), entry.getValue(), totales.get(entry.getKey())));
        }

        List<DebitoEstado> debitos = new ArrayList<>();
        for (Debito debito : debitoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId)) {
            debitos.add(toDebito(debito, cuotaPagos));
        }

        return new EstadoChequera(
                serie.getFacultadId(),
                facultad == null ? null : facultad.getNombre(),
                serie.getTipoChequeraId(),
                tipoChequera.getNombre(),
                serie.getChequeraSerieId(),
                serie.getPersonaId(),
                persona == null ? null : persona.getApellido(),
                persona == null ? null : persona.getNombre(),
                arancelTipo == null ? null : arancelTipo.getDescripcion(),
                lectivo == null ? null : lectivo.getNombre(),
                serie.getBecaPorcentaje(),
                tipoImpresion == null ? null : tipoImpresion.getNombre(),
                alternativaId,
                serie.getHpum() != null && serie.getHpum() != 0,
                productos,
                debitos);
    }

    private ProductoEstado toProducto(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                      Integer alternativaId, Integer productoId, List<ChequeraCuotaPagosDto> cuotas,
                                      ChequeraTotal chequeraTotal) {
        String nombre = cuotas.get(0).getProducto() != null
                ? cuotas.get(0).getProducto().getNombre()
                : "Producto " + productoId;

        // chequera_alternativa: título de la cuota (ej. "Arancel Mensual") y cantidad total de cuotas
        String tituloCuota = nombre;
        Integer totalCuotas = cuotas.size();
        try {
            ChequeraAlternativa alternativa = chequeraAlternativaService.findByUnique(facultadId, tipoChequeraId,
                    chequeraSerieId, productoId, alternativaId);
            tituloCuota = alternativa.getTitulo();
            totalCuotas = alternativa.getCuotas();
        } catch (Exception e) {
            log.debug("Alternativa {} del producto {} no encontrada para el estado de chequera", alternativaId, productoId);
        }

        BigDecimal total = chequeraTotal != null && chequeraTotal.getTotal() != null
                ? chequeraTotal.getTotal() : BigDecimal.ZERO;
        BigDecimal pagado = chequeraTotal != null && chequeraTotal.getPagado() != null
                ? chequeraTotal.getPagado() : BigDecimal.ZERO;

        return new ProductoEstado(productoId, nombre, tituloCuota, totalCuotas, total, pagado,
                cuotas.stream().map(this::toCuota).toList());
    }

    private CuotaEstado toCuota(ChequeraCuotaPagosDto cuota) {
        // chequera_pago es un LEFT JOIN: la cuota puede no tener pagos todavía.
        ChequeraPagoDto pago = (cuota.getChequeraPagos() == null || cuota.getChequeraPagos().isEmpty())
                ? null
                : cuota.getChequeraPagos().get(0);

        // El primer vencimiento es una fecha contractual: se conserva su día calendario, sin convertir el huso horario.
        LocalDate primerVencimiento = cuota.getVencimiento1() != null ? cuota.getVencimiento1().toLocalDate() : null;
        LocalDate fechaPago = pago != null && pago.getFecha() != null
                ? pago.getFecha().withOffsetSameInstant(ZoneOffset.UTC).toLocalDate()
                : null;

        return new CuotaEstado(
                cuota.getCuotaId(),
                cuota.getMes(),
                cuota.getAnho(),
                primerVencimiento,
                cuota.getImporte1() != null ? cuota.getImporte1() : BigDecimal.ZERO,
                pago != null ? pago.getOrden() : null,
                fechaPago,
                pago != null ? pago.getImporte() : null,
                pago != null ? pago.getArchivo() : null);
    }

    private DebitoEstado toDebito(Debito debito, List<ChequeraCuotaPagosDto> cuotaPagos) {
        BigDecimal importe = cuotaPagos.stream()
                .filter(c -> Objects.equals(c.getProductoId(), debito.getProductoId())
                        && Objects.equals(c.getAlternativaId(), debito.getAlternativaId())
                        && Objects.equals(c.getCuotaId(), debito.getCuotaId()))
                .map(c -> c.getImporte1() != null ? c.getImporte1() : BigDecimal.ZERO)
                .findFirst()
                .orElse(BigDecimal.ZERO);

        LocalDate fechaVencimiento = debito.getFechaVencimiento() != null
                ? debito.getFechaVencimiento().withOffsetSameInstant(ZoneOffset.UTC).toLocalDate()
                : null;
        LocalDateTime fechaEnvio = debito.getFechaEnvio() != null
                ? debito.getFechaEnvio().withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime()
                : null;

        String tipoDebito = null;
        try {
            tipoDebito = debitoTipoService.findByDebitoTipoId(debito.getDebitoTipoId()).getNombre();
        } catch (Exception e) {
            log.debug("Tipo de débito {} no encontrado para el estado de chequera", debito.getDebitoTipoId());
        }

        return new DebitoEstado(
                debito.getCuotaId(),
                importe,
                fechaVencimiento,
                debito.getCbu(),
                tipoDebito,
                fechaEnvio,
                debito.getRechazado() != null && debito.getRechazado() != 0,
                debito.getMotivoRechazo());
    }
}