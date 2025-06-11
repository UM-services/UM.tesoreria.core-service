/**
 *
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraCuotaException;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.exception.LectivoException;
import um.tesoreria.core.exception.TipoChequeraException;
import um.tesoreria.core.kotlin.model.*;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.model.dto.DeudaChequera;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;
import um.tesoreria.core.repository.ChequeraCuotaRepository;
import um.tesoreria.core.service.view.ChequeraCuotaDeudaService;
import um.tesoreria.core.util.Tool;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ChequeraCuotaService {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraTotalService chequeraTotalService;
    private final FacultadService facultadService;
    private final TipoChequeraService tipoChequeraService;
    private final LectivoService lectivoService;
    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    public ChequeraCuotaService(ChequeraCuotaRepository repository, ChequeraSerieService chequeraSerieService, ChequeraPagoService chequeraPagoService,
                                ChequeraTotalService chequeraTotalService, FacultadService facultadService, TipoChequeraService tipoChequeraService,
                                LectivoService lectivoService, ChequeraCuotaDeudaService chequeraCuotaDeudaService) {
        this.repository = repository;
        this.chequeraSerieService = chequeraSerieService;
        this.chequeraPagoService = chequeraPagoService;
        this.chequeraTotalService = chequeraTotalService;
        this.facultadService = facultadService;
        this.tipoChequeraService = tipoChequeraService;
        this.lectivoService = lectivoService;
        this.chequeraCuotaDeudaService = chequeraCuotaDeudaService;
    }

    @Transactional
    public List<ChequeraCuota> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        
        // Actualiza y retorna en una sola operación
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId)
                .stream()
                .peek(cuota -> {
                    cuota.setChequeraId(chequeraSerie.getChequeraId());
                    cuota.setChequeraSerie(chequeraSerie);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {

        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        // Encuentra todas las cuotas por facultad, tipo de chequera y serie de chequera
        List<ChequeraCuota> todasCuotas = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);

        // Actualiza chequeraId en todas las cuotas
        todasCuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));

        // Guarda todas las cuotas actualizadas
        repository.saveAll(todasCuotas);

        // Encuentra todas las cuotas filtradas por facultad, tipo de chequera, serie de chequera y alternativa, con ordenación
        List<ChequeraCuota> cuotasFiltradas = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, Sort.by("vencimiento1").ascending()
                        .and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending())));

        // Actualiza chequeraSerie en las cuotas filtradas
        cuotasFiltradas.forEach(cuota -> cuota.setChequeraSerie(chequeraSerie));

        return cuotasFiltradas;
    }

    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdConImporte(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {

        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        // Encuentra todas las cuotas por facultad, tipo de chequera y serie de chequera
        List<ChequeraCuota> todasCuotas = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);

        // Actualiza chequeraId en todas las cuotas
        todasCuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));

        // Guarda todas las cuotas actualizadas
        repository.saveAll(todasCuotas);

        // Encuentra todas las cuotas filtradas por facultad, tipo de chequera, serie de chequera, alternativa y con importe > 0, con ordenación
        List<ChequeraCuota> cuotasFiltradas = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndImporte1GreaterThan(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, BigDecimal.ZERO,
                Sort.by("vencimiento1").ascending().and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending())));

        // Actualiza chequeraSerie en las cuotas filtradas
        cuotasFiltradas.forEach(cuota -> cuota.setChequeraSerie(chequeraSerie));

        return cuotasFiltradas;
    }

    private List<ChequeraCuota> findAllByCuotasActivas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte baja) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndBaja(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, baja);
    }

    private List<ChequeraCuota> findAllByCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte pagado) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndPagado(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, pagado);
    }


    public List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        // Encuentra todas las cuotas por facultad, tipo de chequera y serie de chequera
        List<ChequeraCuota> todasCuotas = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);

        // Actualiza chequeraId en todas las cuotas
        todasCuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));

        // Guarda todas las cuotas actualizadas
        repository.saveAll(todasCuotas);

        // Encuentra todas las cuotas filtradas con los criterios especificados
        List<ChequeraCuota> cuotasFiltradas = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0, OffsetDateTime.now(), BigDecimal.ZERO);

        // Actualiza chequeraSerie en las cuotas filtradas
        cuotasFiltradas.forEach(cuota -> cuota.setChequeraSerie(chequeraSerie));

        return cuotasFiltradas;
    }

    public List<ChequeraCuota> findAllInconsistencias(OffsetDateTime desde, OffsetDateTime hasta, Boolean reduced) {
        final BigDecimal MULTIPLICADOR = new BigDecimal(49);
        
        return chequeraCuotaDeudaService.findAllByRango(desde, hasta, reduced, null, this).stream()
                .map(ChequeraCuotaDeuda::getChequeraCuota).filter(Objects::nonNull)
                .filter(cuota -> {
                    // Extraer validaciones complejas a variables para mejor legibilidad
                    boolean vencimientosInvalidos = Objects.requireNonNull(cuota.getVencimiento1()).isAfter(cuota.getVencimiento2()) ||
                            Objects.requireNonNull(cuota.getVencimiento2()).isAfter(cuota.getVencimiento3());
                            
                    boolean importesInvalidos = cuota.getImporte1().compareTo(cuota.getImporte2()) > 0 ||
                            cuota.getImporte2().compareTo(cuota.getImporte3()) > 0;
                            
                    boolean multiplicadoresInvalidos = Stream.of(
                            new AbstractMap.SimpleEntry<>(cuota.getImporte1Original(), cuota.getImporte1()),
                            new AbstractMap.SimpleEntry<>(cuota.getImporte2Original(), cuota.getImporte2()),
                            new AbstractMap.SimpleEntry<>(cuota.getImporte3Original(), cuota.getImporte3())
                    ).anyMatch(entry -> entry.getKey().multiply(MULTIPLICADOR).compareTo(entry.getValue()) < 0);
                    
                    return vencimientosInvalidos || importesInvalidos || multiplicadoresInvalidos;
                })
                .collect(Collectors.toList());
    }

    public List<ChequeraCuota> findAllPendientes(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBajaAndImporte1GreaterThan(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0, BigDecimal.ZERO);
    }

    public List<ChequeraCuota> findAllPendientesBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBaja(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0);
    }

    public ChequeraCuota findByChequeraCuotaId(Long chequeraCuotaId) {
        ChequeraCuota chequeraCuota = repository.findByChequeraCuotaId(chequeraCuotaId)
                .orElseThrow(() -> new ChequeraCuotaException(chequeraCuotaId));
        if (chequeraCuota.getChequeraId() == null) {
            ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(chequeraCuota.getFacultadId(), chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId());
            chequeraCuota.setChequeraId(chequeraSerie.getChequeraId());
            chequeraCuota = repository.save(chequeraCuota);
            chequeraCuota.setChequeraSerie(chequeraSerie);
        }
        return chequeraCuota;
    }

    public ChequeraCuota findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                      Integer productoId, Integer alternativaId, Integer cuotaId) {
        ChequeraCuota chequeraCuota = repository
                .findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(facultadId,
                        tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .orElseThrow(() -> new ChequeraCuotaException(facultadId, tipoChequeraId, chequeraSerieId, productoId,
                        alternativaId, cuotaId));
        if (chequeraCuota.getChequeraId() == null) {
            ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
            chequeraCuota.setChequeraId(chequeraSerie.getChequeraId());
            chequeraCuota = repository.save(chequeraCuota);
            chequeraCuota.setChequeraSerie(chequeraSerie);
        }
        return chequeraCuota;
    }

    public ChequeraCuota findOneActivaImpagaPrevia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia) {
        ChequeraCuota chequeraCuota = repository
                .findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndImporte1GreaterThanAndVencimiento1LessThanOrderByVencimiento1Desc(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0, BigDecimal.ZERO, referencia)
                .orElseThrow(() -> new ChequeraCuotaException(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));
        if (chequeraCuota.getChequeraId() == null) {
            ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
            chequeraCuota.setChequeraId(chequeraSerie.getChequeraId());
            chequeraCuota = repository.save(chequeraCuota);
            chequeraCuota.setChequeraSerie(chequeraSerie);
        }
        return chequeraCuota;
    }

    @Transactional
    public List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas) {
        chequeraCuotas = repository.saveAll(chequeraCuotas);
        return chequeraCuotas;
    }

    public List<ChequeraCuota> updateBarras(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        // Obtén todas las ChequeraCuota por facultad, tipo de chequera y serie de chequera
        List<ChequeraCuota> chequeraCuotas = this.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);

        // Filtra y actualiza las ChequeraCuota que cumplan con las condiciones
        List<ChequeraCuota> actualizadas = chequeraCuotas.stream()
                .filter(chequeraCuota -> chequeraCuota.getBaja() == 0 &&
                        chequeraCuota.getPagado() == 0 &&
                        chequeraCuota.getImporte1().compareTo(BigDecimal.ZERO) > 0)
                .peek(chequeraCuota -> chequeraCuota.setCodigoBarras(this.calculateCodigoBarras(chequeraCuota)))
                .collect(Collectors.toList());

        // Guarda las ChequeraCuota actualizadas
        return this.saveAll(actualizadas);
    }

    @Transactional
    public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);
    }

    private DeudaChequera createDefaultDeudaChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return new DeudaChequera(
                BigDecimal.ZERO,
                0,
                facultadId,
                "", tipoChequeraId,
                "",
                chequeraSerieId,
                0,
                "",
                1,
                BigDecimal.ZERO,
                new BigDecimal(1000000),
                1000,
                null,
                OffsetDateTime.now(),
                BigDecimal.ZERO
        );
    }

    public DeudaChequera calculateDeuda(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        // Validación temprana de parámetros para evitar procesamiento innecesario
        if (facultadId == null || tipoChequeraId == null || chequeraSerieId == null) {
            log.warn("Parámetros inválidos para calculateDeuda: facultadId={}, tipoChequeraId={}, chequeraSerieId={}", 
                    facultadId, tipoChequeraId, chequeraSerieId);
            return createDefaultDeudaChequera(facultadId, tipoChequeraId, chequeraSerieId);
        }

        ChequeraSerie serie;
        try {
            serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        } catch (ChequeraSerieException e) {
            log.debug("Serie no encontrada para calculateDeuda: facultadId={}, tipoChequeraId={}, chequeraSerieId={}", 
                    facultadId, tipoChequeraId, chequeraSerieId);
            return createDefaultDeudaChequera(facultadId, tipoChequeraId, chequeraSerieId);
        }

        // Optimización con computación paralela: ejecutar consultas en paralelo
        CompletableFuture<Map<String, BigDecimal>> pagosFuture = CompletableFuture.supplyAsync(() ->
                chequeraPagoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, this)
                        .stream()
                        .collect(Collectors.toMap(
                                ChequeraPago::getCuotaKey,
                                ChequeraPago::getImporte,
                                (existing, replacement) -> existing
                        ))
        );

        CompletableFuture<List<ChequeraCuota>> cuotasFuture = CompletableFuture.supplyAsync(() ->
                repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                        facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(),
                        (byte) 0, (byte) 0, Tool.hourAbsoluteArgentina(), BigDecimal.ZERO)
        );

        CompletableFuture<Optional<ChequeraCuota>> cuota1Future = CompletableFuture.supplyAsync(() -> {
            try {
                return repository.findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndCuotaIdOrderByProductoIdDesc(
                        facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(), 1);
            } catch (ChequeraCuotaException e) {
                log.debug("Sin Cuota 1 para serie: {}", chequeraSerieId);
                return Optional.empty();
            }
        });

        CompletableFuture<BigDecimal> totalFuture = CompletableFuture.supplyAsync(() ->
                chequeraTotalService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId)
                        .stream()
                        .map(ChequeraTotal::getTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        try {
            // Esperar a que todas las consultas se completen en paralelo
            Map<String, BigDecimal> pagosMap = pagosFuture.get();
            List<ChequeraCuota> cuotas = cuotasFuture.get();
            Optional<ChequeraCuota> cuota1 = cuota1Future.get();
            BigDecimal total = totalFuture.get();

            // Procesar cuota1
            OffsetDateTime vencimiento1Cuota1 = null;
            BigDecimal importa1Cuota1 = BigDecimal.ZERO;
            if (cuota1.isPresent()) {
                vencimiento1Cuota1 = cuota1.get().getVencimiento1();
                importa1Cuota1 = cuota1.get().getImporte1();
            }

            // Procesar cuotas deudoras de forma más eficiente
            var deudaInfo = cuotas.stream()
                    .map(cuota -> {
                        BigDecimal pagoAplicado = pagosMap.getOrDefault(cuota.cuotaKey(), BigDecimal.ZERO);
                        BigDecimal deudaCuota = cuota.getImporte1()
                                .subtract(pagoAplicado)
                                .max(BigDecimal.ZERO)
                                .setScale(2, RoundingMode.HALF_UP);
                        return new AbstractMap.SimpleEntry<>(deudaCuota, deudaCuota.compareTo(BigDecimal.ZERO) > 0 ? 1 : 0);
                    })
                    .reduce(
                            new AbstractMap.SimpleEntry<>(BigDecimal.ZERO, 0),
                            (acc, curr) -> new AbstractMap.SimpleEntry<>(
                                    acc.getKey().add(curr.getKey()),
                                    acc.getValue() + curr.getValue()
                            )
                    );

            return new DeudaChequera(
                    serie.getPersonaId(),
                    serie.getDocumentoId(),
                    facultadId,
                    "",
                    tipoChequeraId,
                    "",
                    chequeraSerieId,
                    serie.getLectivoId(),
                    "",
                    serie.getAlternativaId(),
                    total,
                    deudaInfo.getKey(),
                    deudaInfo.getValue(),
                    serie.getChequeraId(),
                    vencimiento1Cuota1,
                    importa1Cuota1
            );

        } catch (InterruptedException | ExecutionException e) {
            log.error("Error al procesar consultas paralelas para calculateDeuda: facultadId={}, tipoChequeraId={}, chequeraSerieId={}", 
                    facultadId, tipoChequeraId, chequeraSerieId, e);
            Thread.currentThread().interrupt();
            return createDefaultDeudaChequera(facultadId, tipoChequeraId, chequeraSerieId);
        }
    }

    public DeudaChequera calculateDeudaExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie serie = null;
        try {
            serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        } catch (ChequeraSerieException e) {
            return new DeudaChequera(
                    BigDecimal.ZERO,
                    0,
                    facultadId,
                    "",
                    tipoChequeraId,
                    "",
                    chequeraSerieId,
                    0,
                    "",
                    1,
                    BigDecimal.ZERO,
                    new BigDecimal(1000000),
                    1000,
                    null,
                    OffsetDateTime.now(),
                    BigDecimal.ZERO
            );
        }
        Map<String, ChequeraPago> pagos = chequeraPagoService
                .findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, this).stream()
                .collect(Collectors.toMap(ChequeraPago::getCuotaKey, Function.identity(), (pago, replacement) -> pago));
        BigDecimal deuda = BigDecimal.ZERO;
        Integer cantidad = 0;
        OffsetDateTime vencimiento1Cuota1 = null;
        BigDecimal importa1Cuota1 = BigDecimal.ZERO;
        try {
            var cuota1 = repository.findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndCuotaIdOrderByProductoIdDesc(facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(), 1);
            if (cuota1.isPresent()) {
                vencimiento1Cuota1 = cuota1.get().getVencimiento1();
                importa1Cuota1 = cuota1.get().getImporte1();
            }
        } catch (ChequeraCuotaException e) {
            log.debug("Sin Cuota 1");
        }
        for (ChequeraCuota cuota : repository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                        facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(), (byte) 0, (byte) 0,
                        Tool.hourAbsoluteArgentina(), BigDecimal.ZERO)) {
            BigDecimal deudaCuota = cuota.getImporte1();
            if (pagos.containsKey(cuota.cuotaKey())) {
                deudaCuota = deudaCuota.subtract(pagos.get(cuota.cuotaKey()).getImporte()).setScale(2,
                        RoundingMode.HALF_UP);
            }
            if (deudaCuota.compareTo(BigDecimal.ZERO) < 0) {
                deudaCuota = BigDecimal.ZERO;
            }
            if (deudaCuota.compareTo(BigDecimal.ZERO) > 0) {
                cantidad++;
            }
            deuda = deuda.add(deudaCuota).setScale(2, RoundingMode.HALF_UP);
        }
        BigDecimal total = chequeraTotalService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId).stream()
                .map(ChequeraTotal::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        Facultad facultad = null;
        try {
            facultad = facultadService.findByFacultadId(facultadId);
        } catch (FacultadException e) {
            facultad = new Facultad();
        }
        TipoChequera tipo = null;
        try {
            tipo = tipoChequeraService.findByTipoChequeraId(tipoChequeraId);
        } catch (TipoChequeraException e) {
            tipo = new TipoChequera();
        }
        Lectivo lectivo = null;
        try {
            lectivo = lectivoService.findByLectivoId(serie.getLectivoId());
        } catch (LectivoException e) {
            lectivo = new Lectivo();
        }
        return new DeudaChequera(
                serie.getPersonaId(),
                serie.getDocumentoId(),
                facultadId,
                facultad.getNombre(),
                tipoChequeraId,
                tipo.getNombre(),
                chequeraSerieId,
                serie.getLectivoId(),
                lectivo.getNombre(),
                serie.getAlternativaId(),
                total,
                deuda,
                cantidad,
                serie.getChequeraId(),
                vencimiento1Cuota1,
                importa1Cuota1
        );
    }

    public String calculateCodigoBarras(ChequeraCuota chequeraCuota) {
        String codigoBarras = "";

        if (Objects.requireNonNull(chequeraCuota.getVencimiento3()).isBefore(chequeraCuota.getVencimiento2())) {
            chequeraCuota.setVencimiento2(chequeraCuota.getVencimiento3());
        }
        if (Objects.requireNonNull(chequeraCuota.getVencimiento2()).isBefore(chequeraCuota.getVencimiento1())) {
            chequeraCuota.setVencimiento1(chequeraCuota.getVencimiento2());
        }
        // Codigo Gire
        codigoBarras += "255";
        // Codigo Unidad Academica
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getFacultadId());
        // Tipo de Chequera
        codigoBarras += new DecimalFormat("000").format(chequeraCuota.getTipoChequeraId());
        // Numero de Chequera
        codigoBarras += new DecimalFormat("00000").format(chequeraCuota.getChequeraSerieId());
        // Producto
        codigoBarras += new DecimalFormat("0").format(chequeraCuota.getProductoId());
        // Mes
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getMes());
        // Año
        int anho = chequeraCuota.getAnho();
        if (anho > 2000) {
            anho -= 2000;
        }
        codigoBarras += new DecimalFormat("00").format(anho);
        // Cuota ID
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getCuotaId());
        // 1er Importe
        codigoBarras += new DecimalFormat("0000000").format(chequeraCuota.getImporte1());
        OffsetDateTime vencimiento1 = Objects.requireNonNull(chequeraCuota.getVencimiento1()).plusHours(3);
        OffsetDateTime vencimiento2 = chequeraCuota.getVencimiento2().plusHours(3);
        OffsetDateTime vencimiento3 = chequeraCuota.getVencimiento3().plusHours(3);
        // Dia 1er Vencimiento
        codigoBarras += new DecimalFormat("00").format(vencimiento1.getDayOfMonth());
        log.info("Vencimiento1 -> {}", vencimiento1);
        // Mes 1er Vencimiento
        codigoBarras += new DecimalFormat("00").format(vencimiento1.getMonthValue());
        // Año 1er Vencimiento
        codigoBarras += new DecimalFormat("0000").format(vencimiento1.getYear());
        // Dif 2do Importe
        BigDecimal diferenciaImporte1 = chequeraCuota.getImporte2().subtract(chequeraCuota.getImporte1()).setScale(0, RoundingMode.HALF_UP);
        if (diferenciaImporte1.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia_importe_1 < 0");
        }
        codigoBarras += new DecimalFormat("00000")
                .format(diferenciaImporte1);
        // Dif 2do Vencimiento
        Long diferencia1 = ChronoUnit.DAYS.between(vencimiento1.toLocalDate(), vencimiento2.toLocalDate());
        if (diferencia1 < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia_1 < 0");
        }
        codigoBarras += new DecimalFormat("000").format(diferencia1);
        // Dif 3er Importe
        BigDecimal diferenciaImporte2 = chequeraCuota.getImporte3().subtract(chequeraCuota.getImporte2()).setScale(0, RoundingMode.HALF_UP);
        if (diferenciaImporte2.compareTo(BigDecimal.ZERO) < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia_importe_2 < 0");
        }
        codigoBarras += new DecimalFormat("00000")
                .format(diferenciaImporte2);
        // Dif 3er Vencimiento
        Long diferencia2 = ChronoUnit.DAYS.between(vencimiento2.toLocalDate(), vencimiento3.toLocalDate());
        if (diferencia2 < 0) {
            log.debug("chequera_cuota_service.calculate_codigo_barras.diferencia2 < 0");
        }
        codigoBarras += new DecimalFormat("000").format(diferencia2);
        // Codigo Verificador
        codigoBarras += Tool.calculateVerificadorRapiPago(codigoBarras);

        return codigoBarras;
    }

    public ChequeraCuota update(ChequeraCuota newChequeraCuota, Long chequeraCuotaId) {
        return repository.findByChequeraCuotaId(chequeraCuotaId).map(chequeraCuota -> {
            chequeraCuota = new ChequeraCuota.Builder()
                    .chequeraCuotaId(chequeraCuotaId)
                    .chequeraId(newChequeraCuota.getChequeraId())
                    .facultadId(newChequeraCuota.getFacultadId())
                    .tipoChequeraId(newChequeraCuota.getTipoChequeraId())
                    .chequeraSerieId(newChequeraCuota.getChequeraSerieId())
                    .productoId(newChequeraCuota.getProductoId())
                    .alternativaId(newChequeraCuota.getAlternativaId())
                    .cuotaId(newChequeraCuota.getCuotaId())
                    .mes(newChequeraCuota.getMes())
                    .anho(newChequeraCuota.getAnho())
                    .arancelTipoId(newChequeraCuota.getArancelTipoId())
                    .vencimiento1(newChequeraCuota.getVencimiento1())
                    .importe1(newChequeraCuota.getImporte1())
                    .importe1Original(newChequeraCuota.getImporte1Original())
                    .vencimiento2(newChequeraCuota.getVencimiento2())
                    .importe2(newChequeraCuota.getImporte2())
                    .importe2Original(newChequeraCuota.getImporte2Original())
                    .vencimiento3(newChequeraCuota.getVencimiento3())
                    .importe3(newChequeraCuota.getImporte3())
                    .importe3Original(newChequeraCuota.getImporte3Original())
                    .codigoBarras(newChequeraCuota.getCodigoBarras())
                    .i2Of5(newChequeraCuota.getI2Of5())
                    .pagado(newChequeraCuota.getPagado())
                    .baja(newChequeraCuota.getBaja())
                    .manual(newChequeraCuota.getManual())
                    .compensada(newChequeraCuota.getCompensada())
                    .tramoId(newChequeraCuota.getTramoId())
                    .build();
            return repository.save(chequeraCuota);
        }).orElseThrow(() -> new ChequeraCuotaException(chequeraCuotaId));
    }

    public BigDecimal calculateTotalCuotasActivas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        return findAllByCuotasActivas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, (byte) 0)
                .stream()
                .map(ChequeraCuota::getImporte1)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal calculateTotalCuotasPagadas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId) {
        return findAllByCuotasPagadas(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, (byte) 1)
                .stream()
                .map(ChequeraCuota::getImporte1)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<CuotaPeriodoDto> findAllPeriodosLectivo(Integer lectivoId) {
        return repository.findCuotaPeriodosByLectivoId(lectivoId);
    }

}
