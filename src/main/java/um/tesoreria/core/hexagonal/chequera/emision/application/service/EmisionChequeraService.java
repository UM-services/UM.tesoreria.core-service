package um.tesoreria.core.hexagonal.chequera.emision.application.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.AvisoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorCuota;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.SolicitudEmision;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.NumeradorChequeraPort;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.CodigoI2of5;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service.LectivoCuotaService;
import um.tesoreria.core.hexagonal.chequera.baja.application.service.BajaService;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaPersonaDto;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.InscripcionFullDto;
import um.tesoreria.core.hexagonal.setup.application.service.SetupService;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.kotlin.model.ChequeraAlternativa;
import um.tesoreria.core.model.Debito;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.ChequeraAlternativaService;
import um.tesoreria.core.service.DebitoService;
import um.tesoreria.core.service.LectivoAlternativaService;
import um.tesoreria.core.service.LectivoTotalService;
import um.tesoreria.core.service.facade.ChequeraService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Emisión de la chequera a partir de un borrador.
 *
 * <p>Replica {@code clsCtlChequera.makeChequera} (líneas 1176-1228) y {@code generate}
 * (171-332): numeración, alta/actualización de la serie, cuotas con código de barras,
 * totales, alternativas y débitos, en una única transacción.</p>
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmisionChequeraService {

    private final BorradorEmisionService borradorEmisionService;
    private final BorradorFactory borradorFactory;
    private final BorradorChequeraRepository borradorRepository;
    private final NumeradorChequeraPort numeradorChequeraPort;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraTotalService chequeraTotalService;
    private final ChequeraAlternativaService chequeraAlternativaService;
    private final ChequeraPagoService chequeraPagoService;
    private final LectivoCuotaService lectivoCuotaService;
    private final BajaService bajaService;
    private final LectivoTotalService lectivoTotalService;
    private final LectivoAlternativaService lectivoAlternativaService;
    private final ArancelPorcentajeService arancelPorcentajeService;
    private final TipoChequeraService tipoChequeraService;
    private final PersonaService personaService;
    private final SetupService setupService;
    private final DebitoService debitoService;
    private final ChequeraService chequeraService;
    private final um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService usuarioService;
    /**
     * Hibernate ejecuta los INSERT antes de los DELETE de un mismo flush: al regenerar totales y
     * alternativas hay que forzar el borrado para no chocar con la unique (facultad, tipo, serie,
     * producto).
     */
    private final EntityManager entityManager;

    @Transactional
    public ResultadoEmision emitir(Long borradorId, SolicitudEmision solicitud, Long usuarioId) {
        if (solicitud.getClaveIdempotencia() != null && !solicitud.getClaveIdempotencia().isBlank()) {
            var previo = borradorRepository.findByClaveIdempotencia(solicitud.getClaveIdempotencia());
            if (previo.isPresent()) {
                if (previo.get().getEstado() == EstadoBorrador.EMITIDO) {
                    return resultado(previo.get(), false);
                }
                if (!Objects.equals(previo.get().getBorradorId(), borradorId)) {
                    throw new EmisionException(ReglasEmision.CLAVE_IDEMPOTENCIA_USADA,
                            "La clave de idempotencia ya está asociada a otro borrador");
                }
            }
        }

        BorradorChequera borrador = borradorEmisionService.cargarAbierto(borradorId);
        validarVersion(borrador, solicitud.getVersion());
        TipoChequera tipo = tipoChequeraService.findByTipoChequeraId(borrador.getTipoChequeraId());
        Set<String> claves = borradorEmisionService.claves(usuarioId);
        List<AvisoEmision> avisos = new ArrayList<>();

        if (usuarioId != null && !claves.contains("chequeras.imprimir")
                && !claves.contains("chequeras.emision.individual")) {
            throw new EmisionException(ReglasEmision.SIN_PERMISO_IMPRESION,
                    "El usuario no tiene permiso para imprimir chequeras");
        }

        ChequeraSerie existente = buscarSerie(borrador);
        boolean nueva = existente == null || solicitud.isForzarNueva();
        if (!nueva && esDeBaja(existente)) {
            throw new EmisionException(ReglasEmision.CHEQUERA_DE_BAJA,
                    "La chequera " + existente.getChequeraSerieId()
                            + " está dada de baja: no se puede emitir. Marque «forzar nueva» para generar otra.");
        }
        if (nueva) {
            validarDeuda(borrador, solicitud, claves, usuarioId, avisos);
        }
        if (borrador.getAlternativaId() == null) {
            throw new EmisionException(ReglasEmision.SIN_ALTERNATIVA,
                    "El borrador no tiene alternativa de pago seleccionada");
        }
        Integer curso = resolverCurso(borrador);
        if (curso == null) {
            throw new EmisionException(ReglasEmision.SIN_CURSO,
                    "No se pudo determinar el curso de la persona; ingréselo en el borrador antes de emitir");
        }
        borradorFactory.validar(borrador.getCuotas());

        boolean estabaPagada = existente != null && esPagada(existente);
        if (estabaPagada) {
            avisos.add(AvisoEmision.informativo(ReglasEmision.CHEQUERA_PAGADA,
                    "La chequera " + existente.getChequeraSerieId() + " ya estaba totalmente paga"));
        }

        Long numero = nueva
                ? numeradorChequeraPort.siguienteNumero(borrador.getFacultadId(), borrador.getTipoChequeraId())
                : existente.getChequeraSerieId();
        String login = usuario(solicitud, usuarioId, borrador);
        ChequeraSerie serie = nueva
                ? crearSerie(borrador, tipo, numero, curso, login)
                : actualizarSerie(existente, borrador, tipo, curso, login);

        Long chequeraId = serie.getChequeraId();
        List<ChequeraCuota> cuotas = construirCuotas(borrador, serie, nueva);
        List<ChequeraCuota> guardadas = chequeraCuotaService.saveAll(cuotas);

        regenerarTotales(borrador, serie);
        regenerarAlternativas(borrador, serie);
        actualizarDebitos(serie, guardadas);
        marcarPagos(serie, guardadas);
        chequeraService.track(chequeraId);

        borrador.setEstado(EstadoBorrador.EMITIDO);
        borrador.setChequeraId(chequeraId);
        borrador.setChequeraSerieIdEmitida(numero);
        borrador.setAuditoriaId(UUID.randomUUID().toString());
        borrador.setClaveIdempotencia(solicitud.getClaveIdempotencia());
        borrador.setEmitidoAt(OffsetDateTime.now());
        BorradorChequera emitido = borradorRepository.save(borrador);

        ResultadoEmision resultado = resultado(emitido, nueva);
        resultado.setEstabaPagada(estabaPagada);
        resultado.getAvisos().addAll(avisos);
        return resultado;
    }

    private void validarDeuda(BorradorChequera borrador, SolicitudEmision solicitud, Set<String> claves,
                              Long usuarioId, List<AvisoEmision> avisos) {
        DeudaPersonaDto deuda = null;
        Setup setup = null;
        try {
            deuda = personaService.deudaByPersona(borrador.getPersonaId(), borrador.getDocumentoId());
            setup = setupService.findLastOrFail();
        } catch (RuntimeException e) {
            log.warn("No se pudo validar la deuda de {}/{}: {}", borrador.getPersonaId(),
                    borrador.getDocumentoId(), e.getMessage());
        }
        if (deuda == null || setup == null || deuda.getCuotas() == null || setup.getCuotasPermitidas() == null) {
            return;
        }
        if (deuda.getCuotas() <= setup.getCuotasPermitidas()) {
            return;
        }
        if (!solicitud.isForzarDeuda()) {
            throw new EmisionException(ReglasEmision.DEUDA_EXCEDIDA,
                    "La persona tiene " + deuda.getCuotas() + " cuotas impagas y el tope es "
                            + setup.getCuotasPermitidas() + ". Requiere autorización para forzar la emisión");
        }
        if (usuarioId != null && !claves.contains("chequeras.emision.forzar")) {
            throw new EmisionException(ReglasEmision.SIN_PERMISO_OVERRIDE,
                    "El usuario no tiene permiso para forzar la emisión con deuda excedida");
        }
        if (solicitud.getMotivoOverride() == null || solicitud.getMotivoOverride().isBlank()) {
            throw new EmisionException(ReglasEmision.MOTIVO_OVERRIDE_REQUERIDO,
                    "Debe indicar el motivo por el cual se fuerza la emisión con deuda excedida");
        }
        avisos.add(AvisoEmision.informativo("DEUDA_FORZADA",
                "Emisión forzada con " + deuda.getCuotas() + " cuotas impagas. Motivo: "
                        + solicitud.getMotivoOverride()));
    }

    private ChequeraSerie crearSerie(BorradorChequera borrador, TipoChequera tipo, Long numero, Integer curso,
                                     String login) {
        ChequeraSerie serie = ChequeraSerie.builder()
                .facultadId(borrador.getFacultadId())
                .tipoChequeraId(borrador.getTipoChequeraId())
                .chequeraSerieId(numero)
                .personaId(borrador.getPersonaId())
                .documentoId(borrador.getDocumentoId())
                .lectivoId(borrador.getLectivoId())
                .arancelTipoId(borrador.getArancelTipoId())
                .cursoId(curso)
                .asentado((byte) 0)
                .geograficaId(tipo.getGeograficaId())
                .fecha(borrador.getFecha() == null ? OffsetDateTime.now() : borrador.getFecha())
                .cuotasPagadas(0)
                .alternativaId(borrador.getAlternativaId())
                .algoPagado((byte) 0)
                .tipoImpresionId(ReglasEmision.TIPO_IMPRESION_RAPIPAGO)
                .flagPayperTic((byte) 0)
                .usuarioId(login)
                .enviado((byte) 0)
                .retenida((byte) 0)
                .version(0L)
                .hpum(borrador.getHpum() == null ? (byte) 0 : borrador.getHpum())
                .becaPorcentaje(borrador.getBecaPorcentaje() == null ? BigDecimal.ZERO : borrador.getBecaPorcentaje())
                .becaResolucion(borrador.getBecaResolucion())
                .becaFecha(borrador.getBecaFecha())
                .becaUserId(borrador.getBecaUserId())
                .build();
        return chequeraSerieService.add(serie);
    }

    private ChequeraSerie actualizarSerie(ChequeraSerie serie, BorradorChequera borrador, TipoChequera tipo,
                                          Integer curso, String login) {
        serie.setArancelTipoId(borrador.getArancelTipoId());
        serie.setCursoId(curso);
        serie.setGeograficaId(tipo.getGeograficaId());
        serie.setFecha(borrador.getFecha() == null ? OffsetDateTime.now() : borrador.getFecha());
        serie.setAlternativaId(borrador.getAlternativaId());
        serie.setTipoImpresionId(ReglasEmision.TIPO_IMPRESION_RAPIPAGO);
        serie.setUsuarioId(login);
        serie.setEnviado((byte) 0);
        serie.setHpum(borrador.getHpum() == null ? (byte) 0 : borrador.getHpum());
        serie.setBecaPorcentaje(borrador.getBecaPorcentaje() == null ? BigDecimal.ZERO : borrador.getBecaPorcentaje());
        serie.setBecaResolucion(borrador.getBecaResolucion());
        serie.setBecaFecha(borrador.getBecaFecha());
        serie.setBecaUserId(borrador.getBecaUserId());
        return chequeraSerieService.update(serie, serie.getChequeraId());
    }

    private List<ChequeraCuota> construirCuotas(BorradorChequera borrador, ChequeraSerie serie, boolean nueva) {
        List<LectivoCuota> diseno = lectivoCuotaService.findAllByFacultadLectivoTipo(borrador.getFacultadId(),
                borrador.getLectivoId(), borrador.getTipoChequeraId());
        List<ChequeraCuota> cuotas = new ArrayList<>();
        for (BorradorCuota item : borrador.getCuotas()) {
            ChequeraCuota cuota = ChequeraCuota.builder()
                    .chequeraCuotaId(nueva ? null : item.getChequeraCuotaId())
                    .chequeraId(serie.getChequeraId())
                    .facultadId(borrador.getFacultadId())
                    .tipoChequeraId(borrador.getTipoChequeraId())
                    .chequeraSerieId(serie.getChequeraSerieId())
                    .productoId(item.getProductoId())
                    .alternativaId(item.getAlternativaId())
                    .cuotaId(item.getCuotaId())
                    .mes(item.getMes())
                    .anho(item.getAnio())
                    .arancelTipoId(item.getArancelTipoId() == null ? borrador.getArancelTipoId() : item.getArancelTipoId())
                    .vencimiento1(item.getVencimiento1())
                    .vencimiento2(item.getVencimiento2())
                    .vencimiento3(item.getVencimiento3())
                    .importe1(ReglasEmision.redondear2(item.getImporte1Borrador()))
                    .importe2(ReglasEmision.redondear2(item.getImporte2Borrador()))
                    .importe3(ReglasEmision.redondear2(item.getImporte3Borrador()))
                    .importe1Original(ReglasEmision.redondear2(item.getImporte1Original()))
                    .importe2Original(ReglasEmision.redondear2(item.getImporte2Original()))
                    .importe3Original(ReglasEmision.redondear2(item.getImporte3Original()))
                    .pagado(BorradorCuota.esVerdadero(item.getPagado()) ? (byte) 1 : (byte) 0)
                    .baja(BorradorCuota.esVerdadero(item.getBaja()) ? (byte) 1 : (byte) 0)
                    .manual(BorradorCuota.esVerdadero(item.getManual()) ? (byte) 1 : (byte) 0)
                    .compensada(BorradorCuota.esVerdadero(item.getCompensada()) ? (byte) 1 : (byte) 0)
                    .tramoId(tramoId(diseno, item, item.getTramoId()))
                    .build();
            cuota.setCodigoBarras(chequeraCuotaService.calculateCodigoBarras(cuota));
            cuota.setI2Of5(CodigoI2of5.codificar(cuota.getCodigoBarras()));
            cuotas.add(cuota);
        }
        return cuotas;
    }

    /**
     * Sincroniza los totales de la serie sin borrarlos: {@code chequera_alternativa} los referencia
     * por FK y el legacy sólo podía regenerarlos desactivando los checks de integridad. Se conserva
     * el importe pagado de cada producto y se actualiza el total.
     */
    private void regenerarTotales(BorradorChequera borrador, ChequeraSerie serie) {
        List<LectivoTotal> totales = lectivoTotalService.findAllByTipo(serie.getFacultadId(), serie.getLectivoId(),
                serie.getTipoChequeraId());
        Map<Integer, ChequeraTotal> existentes = chequeraTotalService
                .findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId())
                .stream()
                .collect(Collectors.toMap(ChequeraTotal::getProductoId, total -> total, (primera, segunda) -> primera));

        List<ChequeraTotal> aGuardar = new ArrayList<>();
        for (LectivoTotal total : totales) {
            BigDecimal importe = ReglasEmision.redondear2(ReglasEmision.aplicarPorcentaje(total.getTotal(),
                    porcentaje(borrador.getArancelTipoId(), total.getProductoId())));
            ChequeraTotal actual = existentes.get(total.getProductoId());
            if (actual != null) {
                actual.setTotal(importe);
                aGuardar.add(actual);
            } else {
                aGuardar.add(ChequeraTotal.builder()
                        .facultadId(serie.getFacultadId())
                        .tipoChequeraId(serie.getTipoChequeraId())
                        .chequeraSerieId(serie.getChequeraSerieId())
                        .productoId(total.getProductoId())
                        .total(importe)
                        .pagado(BigDecimal.ZERO)
                        .build());
            }
        }
        if (!aGuardar.isEmpty()) {
            chequeraTotalService.saveAll(aGuardar);
        }
        // Las alternativas referencian a los totales: la fila padre tiene que existir antes.
        entityManager.flush();
    }

    /**
     * Sincroniza las alternativas de la serie en lugar de borrarlas y recrearlas: {@code
     * chequera_cuota} las referencia por FK, así que el legacy sólo podía borrarlas desactivando
     * los checks de integridad. Se actualiza la fila existente y se insertan las faltantes.
     */
    private void regenerarAlternativas(BorradorChequera borrador, ChequeraSerie serie) {
        Map<Integer, ChequeraAlternativa> existentes = chequeraAlternativaService
                .findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(), serie.getChequeraSerieId(),
                        borrador.getAlternativaId())
                .stream()
                .collect(Collectors.toMap(ChequeraAlternativa::getProductoId, alternativa -> alternativa,
                        (primera, segunda) -> primera));

        List<ChequeraAlternativa> aGuardar = new ArrayList<>();
        for (var lectivo : lectivoAlternativaService.findAllByTipo(serie.getFacultadId(), serie.getLectivoId(),
                serie.getTipoChequeraId(), borrador.getAlternativaId())) {
            String titulo = lectivo.getTitulo() == null ? "" : lectivo.getTitulo();
            int cuotas = lectivo.getCuotas() == null ? 0 : lectivo.getCuotas();
            ChequeraAlternativa actual = existentes.get(lectivo.getProductoId());
            if (actual != null) {
                actual.setTitulo(titulo);
                actual.setCuotas(cuotas);
                aGuardar.add(actual);
            } else {
                aGuardar.add(new ChequeraAlternativa(null, serie.getFacultadId(), serie.getTipoChequeraId(),
                        serie.getChequeraSerieId(), lectivo.getProductoId(), lectivo.getAlternativaId(), titulo,
                        cuotas));
            }
        }
        if (!aGuardar.isEmpty()) {
            chequeraAlternativaService.saveAll(aGuardar);
        }
    }

    private void actualizarDebitos(ChequeraSerie serie, List<ChequeraCuota> cuotas) {
        List<Debito> debitos = debitoService.findAllByChequera(serie.getFacultadId(), serie.getTipoChequeraId(),
                serie.getChequeraSerieId());
        if (debitos.isEmpty()) {
            return;
        }
        List<Debito> aGuardar = new ArrayList<>();
        for (Debito debito : debitos) {
            cuotas.stream()
                    .filter(cuota -> Objects.equals(cuota.getProductoId(), debito.getProductoId())
                            && Objects.equals(cuota.getAlternativaId(), debito.getAlternativaId())
                            && Objects.equals(cuota.getCuotaId(), debito.getCuotaId()))
                    .findFirst()
                    .ifPresent(cuota -> {
                        debito.setFechaVencimiento(cuota.getVencimiento1());
                        aGuardar.add(debito);
                    });
        }
        if (!aGuardar.isEmpty()) {
            debitoService.saveAll(aGuardar);
        }
    }

    private void marcarPagos(ChequeraSerie serie, List<ChequeraCuota> cuotas) {
        long pagadas = 0;
        for (ChequeraCuota cuota : cuotas) {
            if (chequeraPagoService.isPagado(serie.getFacultadId(), serie.getTipoChequeraId(),
                    serie.getChequeraSerieId(), cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId())) {
                chequeraPagoService.marcarPago(serie.getFacultadId(), serie.getTipoChequeraId(),
                        serie.getChequeraSerieId(), cuota.getProductoId(), cuota.getAlternativaId(), cuota.getCuotaId());
                pagadas++;
            }
        }
        if (pagadas > 0) {
            serie.setCuotasPagadas((int) pagadas);
            serie.setAlgoPagado((byte) 1);
            chequeraSerieService.update(serie, serie.getChequeraId());
        }
    }

    private ChequeraSerie buscarSerie(BorradorChequera borrador) {
        List<ChequeraSerie> series = chequeraSerieService.findAllByPersonaLectivo(borrador.getPersonaId(),
                borrador.getDocumentoId(), borrador.getLectivoId());
        if (borrador.getChequeraSerieId() != null) {
            return series.stream()
                    .filter(serie -> Objects.equals(serie.getFacultadId(), borrador.getFacultadId())
                            && Objects.equals(serie.getTipoChequeraId(), borrador.getTipoChequeraId())
                            && Objects.equals(serie.getChequeraSerieId(), borrador.getChequeraSerieId()))
                    .findFirst().orElse(null);
        }
        return series.stream()
                .filter(serie -> Objects.equals(serie.getFacultadId(), borrador.getFacultadId())
                        && Objects.equals(serie.getTipoChequeraId(), borrador.getTipoChequeraId()))
                .findFirst().orElse(null);
    }

    private boolean esPagada(ChequeraSerie serie) {
        List<ChequeraCuota> cuotas = chequeraCuotaService
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(serie.getFacultadId(),
                        serie.getTipoChequeraId(), serie.getChequeraSerieId(), serie.getAlternativaId());
        return !cuotas.isEmpty() && cuotas.stream().allMatch(cuota -> BorradorCuota.esVerdadero(cuota.getPagado()));
    }

    private boolean esDeBaja(ChequeraSerie serie) {
        return bajaService.findDatosByUnique(serie.getFacultadId(), serie.getTipoChequeraId(),
                serie.getChequeraSerieId()).isPresent();
    }

    private Integer resolverCurso(BorradorChequera borrador) {
        if (borrador.getCursoId() != null && borrador.getCursoId() > 0) {
            return borrador.getCursoId();
        }
        try {
            InscripcionFullDto inscripcion = personaService.findInscripcionFull(borrador.getFacultadId(),
                    borrador.getPersonaId(), borrador.getDocumentoId(), borrador.getLectivoId());
            if (inscripcion != null && inscripcion.getInscripcion() != null
                    && inscripcion.getInscripcion().getCurso() != null
                    && inscripcion.getInscripcion().getCurso() > 0) {
                return inscripcion.getInscripcion().getCurso();
            }
        } catch (RuntimeException e) {
            log.warn("No se pudo resolver el curso de {}/{}: {}", borrador.getPersonaId(),
                    borrador.getDocumentoId(), e.getMessage());
        }
        return null;
    }

    private Integer tramoId(List<LectivoCuota> diseno, BorradorCuota item, Integer actual) {
        if (actual != null) {
            return actual;
        }
        return diseno.stream()
                .filter(cuota -> Objects.equals(cuota.getProductoId(), item.getProductoId())
                        && Objects.equals(cuota.getAlternativaId(), item.getAlternativaId())
                        && Objects.equals(cuota.getCuotaId(), item.getCuotaId()))
                .map(LectivoCuota::getTramoId)
                .findFirst().orElse(null);
    }

    private BigDecimal porcentaje(Integer arancelTipoId, Integer productoId) {
        try {
            return arancelPorcentajeService.findByUnique(arancelTipoId, productoId).getPorcentaje();
        } catch (RuntimeException e) {
            log.warn("Sin arancel_porcentaje para {}.{}; se usa 100", arancelTipoId, productoId);
            return BigDecimal.valueOf(100);
        }
    }

    private void validarVersion(BorradorChequera borrador, Integer version) {
        if (version != null && borrador.getVersion() != null && !version.equals(borrador.getVersion())) {
            throw new EmisionException(ReglasEmision.BORRADOR_DESACTUALIZADO,
                    "El borrador fue modificado por otra sesión (versión enviada " + version
                            + ", actual " + borrador.getVersion() + ")");
        }
    }

    private ResultadoEmision resultado(BorradorChequera borrador, boolean numeroNuevo) {
        return ResultadoEmision.builder()
                .chequeraId(borrador.getChequeraId())
                .facultadId(borrador.getFacultadId())
                .tipoChequeraId(borrador.getTipoChequeraId())
                .chequeraSerieId(borrador.getChequeraSerieIdEmitida())
                .alternativaId(borrador.getAlternativaId())
                .numeroNuevo(numeroNuevo)
                .borradorEstado(borrador.getEstado())
                .auditoriaId(borrador.getAuditoriaId())
                .build();
    }

    /**
     * {@code chequera_serie.usuario_id} es un login con FK a {@code usuario.login}: nunca se escribe
     * el id numérico. Se resuelve el login del operador y, si no hay identidad, se deja el login del
     * borrador sólo cuando existe en {@code usuario} (NULL es válido).
     */
    private String usuario(SolicitudEmision solicitud, Long usuarioId, BorradorChequera borrador) {
        if (solicitud != null && solicitud.getUsuarioLogin() != null
                && !solicitud.getUsuarioLogin().isBlank()) {
            return solicitud.getUsuarioLogin();
        }
        if (usuarioId != null) {
            try {
                return usuarioService.findByUserId(usuarioId)
                        .map(um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario::getLogin)
                        .orElse(null);
            } catch (RuntimeException e) {
                log.warn("No se pudo resolver el login del usuario {}: {}", usuarioId, e.getMessage());
                return null;
            }
        }
        String delBorrador = borrador.getUsuarioId();
        if (delBorrador == null || delBorrador.isBlank()) {
            return null;
        }
        try {
            return usuarioService.findByLogin(delBorrador).getLogin();
        } catch (RuntimeException e) {
            // Un borrador viejo puede tener un id numérico guardado por error: no violar la FK.
            log.warn("El login '{}' del borrador {} no existe en usuario; se deja usuario_id en NULL",
                    delBorrador, borrador.getBorradorId());
            return null;
        }
    }
}
