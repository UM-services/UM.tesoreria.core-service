package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception.ArchivoYaImportadoException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception.ImportacionPagoException;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoBanco;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ArchivoImportado;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.CanalImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.DebitoCobranza;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.EstadoLinea;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.LineaImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.PreviewImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoCuota;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ReemplazoSerie;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.Rechazo;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoEliminacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.ResultadoReacreditacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.model.SolicitudImportacion;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoBancoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ArchivoImportadoRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.DebitoCobranzaRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ParserArchivoPago;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.PayPerTicRendicionRepository;
import um.tesoreria.core.hexagonal.cobranza.importacionPago.domain.ports.out.ReemplazoRepository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Orquestador de la importación de rendiciones.
 *
 * <p>Composición cross-slice autorizada (patrón {@code estadoChequera}): este servicio usa los
 * <em>application services</em> públicos de {@code chequera/chequeraSerie},
 * {@code chequera/chequeraCuota}, {@code chequera/chequeraPago} y {@code chequera/chequeraTotal}
 * para no duplicar la lógica de marcado de cuotas y totales. No importa entidades ni repositorios
 * de esos slices.</p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ImportacionPagoService {

    private static final int MAX_FILENAME_LENGTH = 50;

    private final List<ParserArchivoPago> parsers;
    private final ArchivoImportadoRepository archivoImportadoRepository;
    private final ArchivoBancoRepository archivoBancoRepository;
    private final ReemplazoRepository reemplazoRepository;
    private final PayPerTicRendicionRepository payPerTicRendicionRepository;
    private final DebitoCobranzaRepository debitoCobranzaRepository;

    // Composición cross-slice autorizada (ver Javadoc de la clase).
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraPagoService chequeraPagoService;

    public PreviewImportacion preview(SolicitudImportacion solicitud) {
        validarSolicitud(solicitud);
        List<LineaImportacion> lineas = parsearYResolver(solicitud);
        return resumir(solicitud, lineas);
    }

    @Transactional
    public ResultadoImportacion importar(SolicitudImportacion solicitud) {
        validarSolicitud(solicitud);
        if (archivoImportadoRepository.existsByNombre(solicitud.filename())) {
            throw new ArchivoYaImportadoException(solicitud.filename());
        }

        List<LineaImportacion> lineas = parsearYResolver(solicitud);
        if (solicitud.canal().isPorPeriodo() && lineas.isEmpty()) {
            throw new ImportacionPagoException("No hay pagos pendientes de importar en el período");
        }
        ArchivoImportado cabecera = archivoImportadoRepository.save(ArchivoImportado.builder()
                .nombre(solicitud.filename())
                .fecha(OffsetDateTime.now())
                .registros(lineas.size())
                .build());

        Set<Integer> aceptadas = solicitud.lineasAceptadas() == null
                ? null
                : new HashSet<>(solicitud.lineasAceptadas());

        int importados = 0;
        int omitidos = 0;
        int noEncontrados = 0;
        int duplicados = 0;
        int rechazados = 0;
        BigDecimal total = BigDecimal.ZERO;
        List<String> errores = new ArrayList<>();

        for (LineaImportacion linea : lineas) {
            // Toda línea del archivo deja su huella en archivo_banco. Si no se imputa queda con
            // pasado = 0 y aparece en "No registrados", igual que en el sistema anterior.
            ArchivoBanco fila = archivoBancoRepository.save(crearFila(linea, solicitud.filename(), solicitud.canal()));
            if (aceptadas != null && !aceptadas.contains(linea.getNumeroLinea())) {
                omitidos++;
                continue;
            }
            EstadoLinea estado = linea.getEstado();
            if (estado == EstadoLinea.NO_ENCONTRADA || estado == EstadoLinea.SIN_ALTERNATIVA) {
                noEncontrados++;
                continue;
            }
            if (estado == EstadoLinea.RECHAZADA) {
                if (solicitud.canal().isDebito() && linea.getDebitoId() != null) {
                    try {
                        imputarRechazo(linea, fila);
                    } catch (RuntimeException e) {
                        throw new ImportacionPagoException("Error al imputar el rechazo de la línea "
                                + linea.getNumeroLinea() + ": " + e.getMessage(), e);
                    }
                }
                rechazados++;
                continue;
            }
            if (estado == EstadoLinea.DUPLICADA && !solicitud.permitirDuplicados()) {
                duplicados++;
                continue;
            }
            if (estado != EstadoLinea.OK && estado != EstadoLinea.DUPLICADA) {
                omitidos++;
                continue;
            }
            try {
                imputar(linea, fila, solicitud.filename(), solicitud.canal());
                importados++;
                total = total.add(importe(linea));
            } catch (RuntimeException e) {
                // El archivo se importa completo o no se importa: cualquier fallo inesperado
                // revierte la transacción y no deja filas a medias.
                throw new ImportacionPagoException("Error al imputar la línea "
                        + linea.getNumeroLinea() + ": " + e.getMessage(), e);
            }
        }

        return ResultadoImportacion.builder()
                .importacionId(cabecera.getId() == null ? null : cabecera.getId().longValue())
                .filename(solicitud.filename())
                .canal(solicitud.canal())
                .registros(lineas.size())
                .importados(importados)
                .omitidos(omitidos)
                .noEncontrados(noEncontrados)
                .duplicados(duplicados)
                .rechazados(rechazados)
                .total(total)
                .errores(errores)
                .build();
    }

    /**
     * Re-acredita una importación de Rapipago con el archivo actualizado.
     *
     * <p>No crea una importación nueva ni pagos: procesa únicamente las líneas con marca
     * {@code A} y actualiza la acreditación de los pagos y las filas de {@code archivo_banco}
     * que ya existían. Es la paridad del botón "Re-Acreditar" del sistema anterior.</p>
     */
    @Transactional
    public ResultadoReacreditacion reacreditar(Integer importacionId, SolicitudImportacion solicitud) {
        validarSolicitud(solicitud);
        if (solicitud.canal() != CanalImportacion.RAPIPAGO) {
            throw new ImportacionPagoException("La re-acreditación sólo aplica al canal Rapipago");
        }
        ArchivoImportado cabecera = archivoImportadoRepository.findById(importacionId)
                .orElseThrow(() -> new ImportacionPagoException("No existe la importación " + importacionId));
        if (!cabecera.getNombre().equalsIgnoreCase(solicitud.filename())) {
            throw new ImportacionPagoException("El archivo no coincide con la importación "
                    + cabecera.getNombre());
        }

        List<LineaImportacion> lineas = parsearYResolver(solicitud);
        Map<ClaveReacreditacion, ArchivoBanco> filas = new HashMap<>();
        archivoBancoRepository.findAllByArchivo(cabecera.getNombre())
                .forEach(fila -> filas.putIfAbsent(clave(fila), fila));

        int registros = 0;
        int actualizados = 0;
        int sinPago = 0;
        int sinRegistro = 0;
        List<String> errores = new ArrayList<>();

        for (LineaImportacion linea : lineas) {
            if (!esMarcaA(linea)) {
                continue;
            }
            registros++;
            if (linea.getEstado() == EstadoLinea.NO_ENCONTRADA
                    || linea.getEstado() == EstadoLinea.SIN_ALTERNATIVA) {
                errores.add("Línea " + linea.getNumeroLinea() + ": " + linea.getMotivo());
                continue;
            }
            ArchivoBanco fila = filas.get(clave(linea));
            if (fila == null) {
                sinRegistro++;
                continue;
            }
            boolean actualizado = linea.isReemplazo()
                    ? actualizarPagoReemplazo(linea, fila)
                    : actualizarPagoNormal(linea, fila);
            if (!actualizado) {
                sinPago++;
                continue;
            }
            fila.setMarcaAcreditacion("A");
            fila.setFechaAcreditacion(linea.getFechaAcreditacion());
            fila.setPasado((byte) 1);
            archivoBancoRepository.save(fila);
            actualizados++;
        }

        return new ResultadoReacreditacion(cabecera.getNombre(), solicitud.canal(), registros,
                actualizados, sinPago, sinRegistro, errores);
    }

    @Transactional
    public ResultadoEliminacion eliminar(Integer importacionId) {
        ArchivoImportado cabecera = archivoImportadoRepository.findById(importacionId)
                .orElseThrow(() -> new ImportacionPagoException("No existe la importación " + importacionId));

        List<ChequeraPago> pagos = chequeraPagoService.findAllByArchivo(cabecera.getNombre());
        List<ReemplazoPago> pagosReemplazo = reemplazoRepository.findAllPagosByArchivo(cabecera.getNombre());

        Set<ClaveCuota> cuotasNormales = new LinkedHashSet<>();
        pagos.forEach(pago -> cuotasNormales.add(ClaveCuota.of(pago.getFacultadId(), pago.getTipoChequeraId(),
                pago.getChequeraSerieId(), pago.getProductoId(), pago.getAlternativaId(), pago.getCuotaId())));

        Set<ClaveCuota> cuotasReemplazo = new LinkedHashSet<>();
        pagosReemplazo.forEach(pago -> cuotasReemplazo.add(ClaveCuota.of(pago.getFacultadId(), pago.getTipoChequeraId(),
                pago.getChequeraSerieId(), pago.getProductoId(), pago.getAlternativaId(), pago.getCuotaId())));

        int archivosBanco = archivoBancoRepository.findAllByArchivo(cabecera.getNombre()).size();

        chequeraPagoService.deleteAllByArchivo(cabecera.getNombre());
        reemplazoRepository.deletePagosByArchivo(cabecera.getNombre());
        archivoBancoRepository.deleteByArchivo(cabecera.getNombre());
        archivoImportadoRepository.delete(cabecera);

        cuotasNormales.forEach(this::recalcularCuotaNormal);
        cuotasReemplazo.forEach(this::recalcularCuotaReemplazo);

        return ResultadoEliminacion.builder()
                .filename(cabecera.getNombre())
                .pagosEliminados(pagos.size())
                .pagosReemplazoEliminados(pagosReemplazo.size())
                .archivosBancoEliminados(archivosBanco)
                .cuotasRecalculadas(cuotasNormales.size() + cuotasReemplazo.size())
                .build();
    }

    public List<ArchivoBanco> noRegistrados(Integer importacionId) {
        ArchivoImportado cabecera = archivoImportadoRepository.findById(importacionId)
                .orElseThrow(() -> new ImportacionPagoException("No existe la importación " + importacionId));
        return archivoBancoRepository.findAllByArchivoAndPasado(cabecera.getNombre(), (byte) 0);
    }

    /**
     * Débitos rechazados de una importación. Se obtienen por el vínculo
     * {@code debito.archivoBancoId} que deja la imputación de rechazos.
     */
    public List<Rechazo> rechazos(Integer importacionId) {
        ArchivoImportado cabecera = archivoImportadoRepository.findById(importacionId)
                .orElseThrow(() -> new ImportacionPagoException("No existe la importación " + importacionId));
        List<ArchivoBanco> filas = archivoBancoRepository.findAllByArchivo(cabecera.getNombre());
        Map<Long, ArchivoBanco> porId = filas.stream()
                .filter(fila -> fila.getId() != null)
                .collect(Collectors.toMap(ArchivoBanco::getId, fila -> fila, (primera, segunda) -> primera));
        return debitoCobranzaRepository.findAllByArchivoBancoIds(porId.keySet()).stream()
                .filter(debito -> debito.getRechazado() != null && debito.getRechazado() == 1)
                .map(debito -> {
                    ArchivoBanco fila = porId.get(debito.getArchivoBancoId());
                    return new Rechazo(debito.getDebitoId(), debito.getFacultadId(), debito.getTipoChequeraId(),
                            debito.getChequeraSerieId(), debito.getProductoId(), debito.getAlternativaId(),
                            debito.getCuotaId(), fila == null ? null : fila.getImporte(),
                            fila == null ? null : fila.getFechaPago(), debito.getMotivoRechazo(),
                            debito.getArchivoBancoId());
                })
                .toList();
    }

    public List<ArchivoImportado> listar(CanalImportacion canal) {
        List<String> nombres = archivoBancoRepository.findDistinctArchivoByTipoPagoIdIn(canal.getTipoPagoIds());
        if (nombres.isEmpty()) {
            return List.of();
        }
        return archivoImportadoRepository.findAllByNombreIn(nombres).stream()
                .sorted(Comparator.comparing(ArchivoImportado::getFecha,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();
    }

    public ArchivoImportado findImportacion(Integer importacionId) {
        return archivoImportadoRepository.findById(importacionId)
                .orElseThrow(() -> new ImportacionPagoException("No existe la importación " + importacionId));
    }

    private void validarSolicitud(SolicitudImportacion solicitud) {
        if (solicitud == null || solicitud.canal() == null) {
            throw new ImportacionPagoException("Canal de importación requerido");
        }
        if (solicitud.filename() == null || solicitud.filename().isBlank()) {
            throw new ImportacionPagoException("Nombre de archivo requerido");
        }
        if (solicitud.filename().length() > MAX_FILENAME_LENGTH) {
            throw new ImportacionPagoException("El nombre del archivo supera los "
                    + MAX_FILENAME_LENGTH + " caracteres permitidos");
        }
        if (solicitud.canal().isPorPeriodo()) {
            if (solicitud.desde() == null || solicitud.hasta() == null) {
                throw new ImportacionPagoException("El período desde/hasta es obligatorio para el canal "
                        + solicitud.canal());
            }
            if (solicitud.desde().isAfter(solicitud.hasta())) {
                throw new ImportacionPagoException("El período es inválido: desde es posterior a hasta");
            }
            return;
        }
        if (solicitud.contenido() == null || solicitud.contenido().length == 0) {
            throw new ImportacionPagoException("El archivo está vacío");
        }
    }

    private ParserArchivoPago parser(CanalImportacion canal) {
        return parsers.stream()
                .filter(parser -> parser.canal() == canal)
                .findFirst()
                .orElseThrow(() -> new ImportacionPagoException("No hay parser para el canal " + canal));
    }

    private List<LineaImportacion> parsearYResolver(SolicitudImportacion solicitud) {
        ParserArchivoPago parser = parser(solicitud.canal());
        List<LineaImportacion> lineas = new ArrayList<>(parser.soportaPeriodo()
                ? parser.porPeriodo(solicitud.desde(), solicitud.hasta())
                : parser.parsear(solicitud.filename(), solicitud.contenido()));
        lineas.forEach(linea -> resolver(linea, solicitud.canal()));
        return lineas;
    }

    private PreviewImportacion resumir(SolicitudImportacion solicitud, List<LineaImportacion> lineas) {
        BigDecimal total = lineas.stream().map(this::importe).reduce(BigDecimal.ZERO, BigDecimal::add);
        int perdidos = (int) lineas.stream()
                .filter(linea -> linea.getEstado() == EstadoLinea.NO_ENCONTRADA
                        || linea.getEstado() == EstadoLinea.SIN_ALTERNATIVA)
                .count();
        return PreviewImportacion.builder()
                .filename(solicitud.filename())
                .canal(solicitud.canal())
                .yaImportada(archivoImportadoRepository.existsByNombre(solicitud.filename()))
                .registros(lineas.size())
                .total(total)
                .encontrados((int) lineas.stream().filter(linea -> linea.getEstado() == EstadoLinea.OK).count())
                .perdidos(perdidos)
                .duplicados((int) lineas.stream()
                        .filter(linea -> linea.getEstado() == EstadoLinea.DUPLICADA).count())
                .rechazados((int) lineas.stream()
                        .filter(linea -> linea.getEstado() == EstadoLinea.RECHAZADA).count())
                .lineas(lineas)
                .build();
    }

    private void resolver(LineaImportacion linea, CanalImportacion canal) {
        if (canal == CanalImportacion.DEBITO_DIRECTO && !resolverDebitoDirecto(linea)) {
            return;
        }
        if (canal == CanalImportacion.DEBITO_VISA && !resolverDebitoVisa(linea)) {
            return;
        }
        if (linea.isRechazado()) {
            linea.setEstado(EstadoLinea.RECHAZADA);
            linea.setMotivo(Objects.toString(linea.getMotivoRechazo(), "").isBlank()
                    ? "RECHAZO MOTIVO DESCONOCIDO" : linea.getMotivoRechazo());
            return;
        }

        ChequeraSerie serie;
        try {
            serie = chequeraSerieService.findByUnique(linea.getFacultadId(), linea.getTipoChequeraId(),
                    linea.getChequeraSerieId());
            linea.setReemplazo(false);
            completarSerie(linea, serie);
        } catch (ChequeraSerieException e) {
            var serieReemplazo = reemplazoRepository.findSerieByUnique(linea.getFacultadId(),
                    linea.getTipoChequeraId(), linea.getChequeraSerieId());
            if (serieReemplazo.isEmpty()) {
                linea.setEstado(EstadoLinea.NO_ENCONTRADA);
                linea.setMotivo("No existe la chequera");
                return;
            }
            linea.setReemplazo(true);
            completarSerie(linea, serieReemplazo.get());
        }

        if (canal == CanalImportacion.RAPIPAGO) {
            resolverAlternativaRapipago(linea);
        }

        if (linea.getAlternativaId() == null || linea.getAlternativaId() == 0) {
            linea.setEstado(EstadoLinea.SIN_ALTERNATIVA);
            linea.setMotivo("La chequera no tiene alternativa de arancel");
            return;
        }

        DatosCuota cuota = buscarCuota(linea);
        if (cuota == null) {
            linea.setEstado(EstadoLinea.NO_ENCONTRADA);
            linea.setMotivo("No existe la cuota");
            return;
        }
        linea.setMes(cuota.mes());
        linea.setAnho(cuota.anho());
        linea.setVencimiento(cuota.vencimiento1());
        linea.setImporteCuota(cuota.importe1());
        linea.setProductoNombre(cuota.productoNombre());
        linea.setEstado(esDuplicada(linea, canal) ? EstadoLinea.DUPLICADA : EstadoLinea.OK);
    }

    /**
     * Rapipago trae las claves en la barra pero no la alternativa. Se usa la de la serie y, si no
     * tiene, la de la cuota cuyo importe coincide con el del pago (importe1/2/3). Si tampoco hay
     * coincidencia se replica el default histórico de la alternativa 1.
     */
    private boolean resolverDebitoDirecto(LineaImportacion linea) {
        if (linea.getDebitoId() == null) {
            linea.setEstado(EstadoLinea.NO_ENCONTRADA);
            linea.setMotivo("El archivo no trae el identificador de débito");
            return false;
        }
        var debito = debitoCobranzaRepository.findById(linea.getDebitoId());
        if (debito.isEmpty()) {
            linea.setEstado(EstadoLinea.NO_ENCONTRADA);
            linea.setMotivo("No existe el débito " + linea.getDebitoId());
            return false;
        }
        completarDebito(linea, debito.get());
        return true;
    }

    private boolean resolverDebitoVisa(LineaImportacion linea) {
        var debito = debitoCobranzaRepository.findByCuota(linea.getFacultadId(), linea.getTipoChequeraId(),
                linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(), linea.getCuotaId(),
                CanalImportacion.DEBITO_VISA.getDebitoTipoId());
        if (debito.isEmpty()) {
            linea.setEstado(EstadoLinea.NO_ENCONTRADA);
            linea.setMotivo("No existe el débito para la cuota del cupón");
            return false;
        }
        completarDebito(linea, debito.get());
        return true;
    }

    private void completarDebito(LineaImportacion linea, DebitoCobranza debito) {
        linea.setDebitoId(debito.getDebitoId());
        linea.setFacultadId(debito.getFacultadId());
        linea.setTipoChequeraId(debito.getTipoChequeraId());
        linea.setChequeraSerieId(debito.getChequeraSerieId());
        linea.setProductoId(debito.getProductoId());
        linea.setAlternativaId(debito.getAlternativaId());
        linea.setCuotaId(debito.getCuotaId());
    }

    private void resolverAlternativaRapipago(LineaImportacion linea) {
        if (linea.getAlternativaId() != null && linea.getAlternativaId() != 0) {
            return;
        }
        if (linea.isReemplazo()) {
            Integer alternativa = alternativaPorImporteReemplazo(linea);
            linea.setAlternativaId(alternativa == null ? 0 : alternativa);
        } else {
            Integer alternativa = alternativaPorImporteNormal(linea);
            linea.setAlternativaId(alternativa == null ? 1 : alternativa);
        }
    }

    private Integer alternativaPorImporteNormal(LineaImportacion linea) {
        return chequeraCuotaService.findAllByChequera(linea.getFacultadId(), linea.getTipoChequeraId(),
                        linea.getChequeraSerieId()).stream()
                .filter(cuota -> Objects.equals(cuota.getProductoId(), linea.getProductoId()))
                .filter(cuota -> Objects.equals(cuota.getCuotaId(), linea.getCuotaId()))
                .filter(cuota -> Objects.equals(cuota.getMes(), linea.getMes()))
                .filter(cuota -> Objects.equals(cuota.getAnho(), linea.getAnho()))
                .filter(cuota -> coincideImporte(linea.getImporte(), cuota.getImporte1(),
                        cuota.getImporte2(), cuota.getImporte3()))
                .map(ChequeraCuota::getAlternativaId)
                .findFirst()
                .orElse(null);
    }

    private Integer alternativaPorImporteReemplazo(LineaImportacion linea) {
        return reemplazoRepository.findCuotasByPeriodo(linea.getFacultadId(), linea.getTipoChequeraId(),
                        linea.getChequeraSerieId(), linea.getProductoId(), linea.getCuotaId(),
                        linea.getMes(), linea.getAnho()).stream()
                .filter(cuota -> coincideImporte(linea.getImporte(), cuota.getImporte1(),
                        cuota.getImporte2(), cuota.getImporte3()))
                .map(ReemplazoCuota::getAlternativaId)
                .findFirst()
                .orElse(null);
    }

    private boolean coincideImporte(BigDecimal importe, BigDecimal... candidatos) {
        if (importe == null) {
            return false;
        }
        for (BigDecimal candidato : candidatos) {
            if (candidato != null && candidato.compareTo(importe) == 0) {
                return true;
            }
        }
        return false;
    }

    private void completarSerie(LineaImportacion linea, ChequeraSerie serie) {
        if (linea.getAlternativaId() == null || linea.getAlternativaId() == 0) {
            linea.setAlternativaId(serie.getAlternativaId());
        }
        linea.setFacultadNombre(nombre(serie.getFacultad()));
        linea.setTipoChequeraNombre(nombre(serie.getTipoChequera()));
        linea.setGeograficaNombre(nombre(serie.getGeografica()));
        linea.setPersonaNombre(nombrePersona(serie));
    }

    private void completarSerie(LineaImportacion linea, ReemplazoSerie serie) {
        if (linea.getAlternativaId() == null || linea.getAlternativaId() == 0) {
            linea.setAlternativaId(serie.getAlternativaId());
        }
    }

    private DatosCuota buscarCuota(LineaImportacion linea) {
        try {
            ChequeraCuota cuota = chequeraCuotaService.findByUnique(linea.getFacultadId(),
                    linea.getTipoChequeraId(), linea.getChequeraSerieId(), linea.getProductoId(),
                    linea.getAlternativaId(), linea.getCuotaId());
            return new DatosCuota(cuota.getMes(), cuota.getAnho(), cuota.getVencimiento1(),
                    cuota.getImporte1(), nombre(cuota.getProducto()));
        } catch (ChequeraCuotaException e) {
            return reemplazoRepository.findCuotaByUnique(linea.getFacultadId(), linea.getTipoChequeraId(),
                            linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(),
                            linea.getCuotaId())
                    .map(cuota -> new DatosCuota(cuota.getMes(), cuota.getAnho(), cuota.getVencimiento1(),
                            cuota.getImporte1(), null))
                    .orElse(null);
        }
    }

    private boolean esDuplicada(LineaImportacion linea, CanalImportacion canal) {
        if (canal == CanalImportacion.RAPIPAGO) {
            // Una línea A actualiza la acreditación del pago existente; no genera un pago nuevo.
            if (esAcreditado(linea)) {
                return false;
            }
            return archivoBancoRepository.existsByPagoConVerificador(
                    String.valueOf(linea.getFacultadId()),
                    identificacion(linea),
                    linea.getProductoId(),
                    linea.getMes(),
                    linea.getAnho(),
                    linea.getCuotaId(),
                    importe(linea),
                    linea.getFechaPago(),
                    linea.getVerificador());
        }
        return archivoBancoRepository.existsByPago(
                String.valueOf(linea.getFacultadId()),
                identificacion(linea),
                linea.getProductoId(),
                linea.getMes(),
                linea.getAnho(),
                linea.getCuotaId(),
                importeArchivoBanco(linea, canal),
                linea.getFechaPago(),
                linea.getMarcaAcreditacion());
    }

    private ArchivoBanco crearFila(LineaImportacion linea, String filename, CanalImportacion canal) {
        boolean acreditado = !esRapipago(linea) || esMarcaA(linea);
        return ArchivoBanco.builder()
                .codigoEmpresa(String.valueOf(linea.getFacultadId()))
                .identificacion(identificacion(linea))
                .productoId(linea.getProductoId())
                .mesPago(linea.getMes())
                .anhoPago(linea.getAnho())
                .cuotaId(linea.getCuotaId())
                .importe(importeArchivoBanco(linea, canal))
                .fechaPago(linea.getFechaPago())
                .marcaAcreditacion(linea.getMarcaAcreditacion())
                .fechaAcreditacion(acreditado ? linea.getFechaAcreditacion() : null)
                .verificador(linea.getVerificador())
                .path("")
                .archivo(filename)
                .pasado((byte) 0)
                .tipoPagoId(linea.getTipoPagoId())
                .repetido(linea.getEstado() == EstadoLinea.DUPLICADA ? (byte) 1 : (byte) 0)
                .build();
    }

    private void imputar(LineaImportacion linea, ArchivoBanco fila, String filename, CanalImportacion canal) {
        if (linea.isReemplazo()) {
            imputarReemplazo(linea, fila, filename, canal);
        } else {
            imputarNormal(linea, fila, filename, canal);
        }
        if (canal.isDebito() && linea.getDebitoId() != null) {
            debitoCobranzaRepository.marcarArchivoBanco(linea.getDebitoId(), fila.getId());
        }
        if (canal == CanalImportacion.PAY_PER_TIC && linea.getPayperticId() != null) {
            payPerTicRendicionRepository.marcarImportado(linea.getPayperticId());
        }
        fila.setPasado((byte) 1);
        archivoBancoRepository.save(fila);
    }

    private void imputarRechazo(LineaImportacion linea, ArchivoBanco fila) {
        String motivo = Objects.toString(linea.getMotivoRechazo(), "").isBlank()
                ? "RECHAZO MOTIVO DESCONOCIDO" : linea.getMotivoRechazo().trim();
        debitoCobranzaRepository.marcarRechazado(linea.getDebitoId(), motivo, fila.getId());
        fila.setPasado((byte) 1);
        archivoBancoRepository.save(fila);
    }

    private void imputarNormal(LineaImportacion linea, ArchivoBanco fila, String filename, CanalImportacion canal) {
        boolean rapipago = canal == CanalImportacion.RAPIPAGO;
        boolean acreditado = !rapipago || esAcreditado(linea);
        if (canal == CanalImportacion.DEBITO_VISA && !linea.isReemplazo()) {
            bajarImporteCuotaVisa(linea);
        }
        if (rapipago) {
            Optional<ChequeraPago> existente = buscarPagoNormal(linea);
            if (existente.isPresent()) {
                ChequeraPago pago = existente.get();
                if (acreditado) {
                    pago.setAcreditacion(linea.getFechaAcreditacion());
                    pago.setArchivoBancoIdAcreditacion(fila.getId());
                }
                chequeraPagoService.update(pago);
            } else {
                chequeraPagoService.create(pagoNormal(linea, fila, filename, acreditado));
            }
        } else {
            chequeraPagoService.create(pagoNormal(linea, fila, filename, true));
        }
        recalcularCuotaNormal(ClaveCuota.of(linea.getFacultadId(), linea.getTipoChequeraId(),
                linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(), linea.getCuotaId()));
    }

    /**
     * Regla histórica de VISA: si la cuota normal tiene un importe mayor al que informó el banco,
     * se baja {@code importe1} al importe del archivo (sin guardar el valor previo). Sólo aplica a
     * la cuota normal; la de reemplazo conserva su importe, igual que el sistema anterior.
     */
    private void bajarImporteCuotaVisa(LineaImportacion linea) {
        ChequeraCuota cuota = chequeraCuotaService.findByUnique(linea.getFacultadId(), linea.getTipoChequeraId(),
                linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(), linea.getCuotaId());
        if (cuota.getImporte1() != null && importe(linea).compareTo(cuota.getImporte1()) < 0) {
            cuota.setImporte1(importe(linea));
            chequeraCuotaService.update(cuota, cuota.getChequeraCuotaId());
        }
    }

    private ChequeraPago pagoNormal(LineaImportacion linea, ArchivoBanco fila, String filename, boolean acreditado) {
        return ChequeraPago.builder()
                .facultadId(linea.getFacultadId())
                .tipoChequeraId(linea.getTipoChequeraId())
                .chequeraSerieId(linea.getChequeraSerieId())
                .productoId(linea.getProductoId())
                .alternativaId(linea.getAlternativaId())
                .cuotaId(linea.getCuotaId())
                .orden(chequeraPagoService.nextOrden(linea.getFacultadId(), linea.getTipoChequeraId(),
                        linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(), linea.getCuotaId()))
                .mes(linea.getMes())
                .anho(linea.getAnho())
                .fecha(linea.getFechaPago())
                .acreditacion(acreditado ? linea.getFechaAcreditacion() : null)
                .importe(importe(linea))
                .path("")
                .archivo(filename)
                .observaciones("")
                .archivoBancoId(fila.getId())
                .archivoBancoIdAcreditacion(acreditado ? fila.getId() : null)
                .verificador(verificador(linea))
                .tipoPagoId(linea.getTipoPagoId())
                .build();
    }

    private void imputarReemplazo(LineaImportacion linea, ArchivoBanco fila, String filename, CanalImportacion canal) {
        boolean rapipago = canal == CanalImportacion.RAPIPAGO;
        boolean acreditado = !rapipago || esAcreditado(linea);
        if (rapipago) {
            Optional<ReemplazoPago> existente = buscarPagoReemplazo(linea);
            if (existente.isPresent()) {
                ReemplazoPago pago = existente.get();
                if (acreditado) {
                    pago.setAcreditacion(linea.getFechaAcreditacion());
                    pago.setArchivoBancoIdAcreditacion(fila.getId());
                }
                reemplazoRepository.savePago(pago);
            } else {
                reemplazoRepository.savePago(pagoReemplazo(linea, fila, filename, acreditado));
            }
        } else {
            reemplazoRepository.savePago(pagoReemplazo(linea, fila, filename, true));
        }
        recalcularCuotaReemplazo(ClaveCuota.of(linea.getFacultadId(), linea.getTipoChequeraId(),
                linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(), linea.getCuotaId()));
    }

    private ReemplazoPago pagoReemplazo(LineaImportacion linea, ArchivoBanco fila, String filename, boolean acreditado) {
        return ReemplazoPago.builder()
                .facultadId(linea.getFacultadId())
                .tipoChequeraId(linea.getTipoChequeraId())
                .chequeraSerieId(linea.getChequeraSerieId())
                .productoId(linea.getProductoId())
                .alternativaId(linea.getAlternativaId())
                .cuotaId(linea.getCuotaId())
                .orden(reemplazoRepository.nextOrden(linea.getFacultadId(), linea.getTipoChequeraId(),
                        linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(), linea.getCuotaId()))
                .mes(linea.getMes())
                .anho(linea.getAnho())
                .fecha(linea.getFechaPago())
                .acreditacion(acreditado ? linea.getFechaAcreditacion() : null)
                .importe(importe(linea))
                .path("")
                .archivo(filename)
                .observaciones("")
                .archivoBancoId(fila.getId())
                .archivoBancoIdAcreditacion(acreditado ? fila.getId() : null)
                .verificador(verificador(linea))
                .tipoPagoId(linea.getTipoPagoId())
                .build();
    }

    private Optional<ChequeraPago> buscarPagoNormal(LineaImportacion linea) {
        return chequeraPagoService.findAllByCuota(linea.getFacultadId(), linea.getTipoChequeraId(),
                        linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(),
                        linea.getCuotaId()).stream()
                .filter(pago -> mismaFecha(pago.getFecha(), linea.getFechaPago()))
                .filter(pago -> Objects.equals(pago.getVerificador(), verificador(linea)))
                .findFirst();
    }

    private Optional<ReemplazoPago> buscarPagoReemplazo(LineaImportacion linea) {
        return reemplazoRepository.findAllPagosByCuota(linea.getFacultadId(), linea.getTipoChequeraId(),
                        linea.getChequeraSerieId(), linea.getProductoId(), linea.getAlternativaId(),
                        linea.getCuotaId()).stream()
                .filter(pago -> mismaFecha(pago.getFecha(), linea.getFechaPago()))
                .filter(pago -> Objects.equals(pago.getVerificador(), verificador(linea)))
                .findFirst();
    }

    private boolean actualizarPagoNormal(LineaImportacion linea, ArchivoBanco fila) {
        Optional<ChequeraPago> pago = buscarPagoNormal(linea);
        if (pago.isEmpty()) {
            return false;
        }
        pago.get().setAcreditacion(linea.getFechaAcreditacion());
        pago.get().setArchivoBancoIdAcreditacion(fila.getId());
        chequeraPagoService.update(pago.get());
        return true;
    }

    private boolean actualizarPagoReemplazo(LineaImportacion linea, ArchivoBanco fila) {
        Optional<ReemplazoPago> pago = buscarPagoReemplazo(linea);
        if (pago.isEmpty()) {
            return false;
        }
        pago.get().setAcreditacion(linea.getFechaAcreditacion());
        pago.get().setArchivoBancoIdAcreditacion(fila.getId());
        reemplazoRepository.savePago(pago.get());
        return true;
    }

    private boolean mismaFecha(OffsetDateTime una, OffsetDateTime otra) {
        if (una == null || otra == null) {
            return una == otra;
        }
        return una.toLocalDate().equals(otra.toLocalDate());
    }

    private void recalcularCuotaNormal(ClaveCuota clave) {
        chequeraPagoService.marcarPago(clave.facultadId(), clave.tipoChequeraId(), clave.chequeraSerieId(),
                clave.productoId(), clave.alternativaId(), clave.cuotaId());
    }

    private void recalcularCuotaReemplazo(ClaveCuota clave) {
        reemplazoRepository.findCuotaByUnique(clave.facultadId(), clave.tipoChequeraId(),
                        clave.chequeraSerieId(), clave.productoId(), clave.alternativaId(), clave.cuotaId())
                .ifPresent(cuota -> {
                    boolean pagado = reemplazoRepository.existsPagoByCuota(clave.facultadId(),
                            clave.tipoChequeraId(), clave.chequeraSerieId(), clave.productoId(),
                            clave.alternativaId(), clave.cuotaId());
                    cuota.setPagado((byte) (pagado ? 1 : 0));
                    reemplazoRepository.saveCuota(cuota);
                    reemplazoRepository.recalculateTotal(clave.facultadId(), clave.tipoChequeraId(),
                            clave.chequeraSerieId(), clave.productoId(), clave.alternativaId());
                });
    }

    private boolean esRapipago(LineaImportacion linea) {
        return linea.getTipoPagoId() != null && linea.getTipoPagoId() == 3;
    }

    private boolean esAcreditado(LineaImportacion linea) {
        // El sistema anterior trataba como acreditado todo lo que no fuera "P".
        return !"P".equalsIgnoreCase(Objects.toString(linea.getMarcaAcreditacion(), ""));
    }

    private boolean esMarcaA(LineaImportacion linea) {
        return "A".equalsIgnoreCase(Objects.toString(linea.getMarcaAcreditacion(), ""));
    }

    private Integer verificador(LineaImportacion linea) {
        String valor = linea.getVerificador();
        if (valor == null || valor.isBlank()) {
            return 0;
        }
        try {
            return Integer.valueOf(valor.trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Integer identificacion(LineaImportacion linea) {
        if (linea.getTipoChequeraId() == null || linea.getChequeraSerieId() == null) {
            return null;
        }
        return linea.getTipoChequeraId() * 100_000 + linea.getChequeraSerieId().intValue();
    }

    private BigDecimal importe(LineaImportacion linea) {
        return linea.getImporte() == null ? BigDecimal.ZERO : linea.getImporte();
    }

    /**
     * Importe que se guarda en {@code archivo_banco}. Para los canales de débito el sistema
     * anterior guardaba el importe de la cuota (no el del archivo); se replica esa paridad.
     */
    private BigDecimal importeArchivoBanco(LineaImportacion linea, CanalImportacion canal) {
        if (canal.isDebito() && linea.getImporteCuota() != null) {
            return linea.getImporteCuota();
        }
        return importe(linea);
    }

    private String nombre(um.tesoreria.core.hexagonal.dependencias.facultad.domain.model.Facultad facultad) {
        return facultad == null ? null : facultad.getNombre();
    }

    private String nombre(um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera tipo) {
        return tipo == null ? null : tipo.getNombre();
    }

    private String nombre(um.tesoreria.core.hexagonal.dependencias.geografica.domain.model.Geografica geografica) {
        return geografica == null ? null : geografica.getNombre();
    }

    private String nombre(um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto producto) {
        return producto == null ? null : producto.getNombre();
    }

    private String nombrePersona(ChequeraSerie serie) {
        if (serie.getPersona() == null) {
            return null;
        }
        String apellido = Objects.toString(serie.getPersona().getApellido(), "");
        String nombre = Objects.toString(serie.getPersona().getNombre(), "");
        return (apellido + ", " + nombre).trim();
    }

    private ClaveReacreditacion clave(LineaImportacion linea) {
        return new ClaveReacreditacion(
                codigoEmpresa(String.valueOf(linea.getFacultadId())),
                identificacion(linea),
                linea.getProductoId(),
                linea.getCuotaId(),
                importe(linea).stripTrailingZeros().toPlainString(),
                linea.getFechaPago() == null ? null : linea.getFechaPago().toLocalDate().toString(),
                Objects.toString(linea.getVerificador(), "").trim());
    }

    private ClaveReacreditacion clave(ArchivoBanco fila) {
        BigDecimal importe = fila.getImporte() == null ? BigDecimal.ZERO : fila.getImporte();
        return new ClaveReacreditacion(
                codigoEmpresa(fila.getCodigoEmpresa()),
                fila.getIdentificacion(),
                fila.getProductoId(),
                fila.getCuotaId(),
                importe.stripTrailingZeros().toPlainString(),
                fila.getFechaPago() == null ? null : fila.getFechaPago().toLocalDate().toString(),
                Objects.toString(fila.getVerificador(), "").trim());
    }

    private String codigoEmpresa(String value) {
        if (value == null) {
            return null;
        }
        String limpio = value.trim();
        try {
            return String.valueOf(Integer.parseInt(limpio));
        } catch (NumberFormatException e) {
            return limpio;
        }
    }

    private record DatosCuota(Integer mes, Integer anho, OffsetDateTime vencimiento1,
                              BigDecimal importe1, String productoNombre) {
    }

    private record ClaveReacreditacion(String codigoEmpresa, Integer identificacion, Integer productoId,
                                       Integer cuotaId, String importe, String fechaPago, String verificador) {
    }

    private record ClaveCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                              Integer productoId, Integer alternativaId, Integer cuotaId) {

        static ClaveCuota of(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                             Integer productoId, Integer alternativaId, Integer cuotaId) {
            return new ClaveCuota(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        }
    }

}
