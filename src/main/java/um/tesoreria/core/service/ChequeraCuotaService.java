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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
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
import um.tesoreria.core.repository.IChequeraCuotaRepository;
import um.tesoreria.core.service.view.ChequeraCuotaDeudaService;
import um.tesoreria.core.util.Tool;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ChequeraCuotaService {

    private final IChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraTotalService chequeraTotalService;
    private final FacultadService facultadService;
    private final TipoChequeraService tipoChequeraService;
    private final LectivoService lectivoService;
    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    public ChequeraCuotaService(IChequeraCuotaRepository repository, ChequeraSerieService chequeraSerieService, ChequeraPagoService chequeraPagoService,
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
                .map(ChequeraCuotaDeuda::getChequeraCuota)
                .filter(cuota -> {
                    // Extraer validaciones complejas a variables para mejor legibilidad
                    boolean vencimientosInvalidos = cuota.getVencimiento1().isAfter(cuota.getVencimiento2()) ||
                            cuota.getVencimiento2().isAfter(cuota.getVencimiento3());
                            
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
                BigDecimal.ZERO, 0, facultadId, "", tipoChequeraId, "", 
                chequeraSerieId, 0, "", 1, BigDecimal.ZERO, 
                new BigDecimal(1000000), 1000, null
        );
    }

    public DeudaChequera calculateDeuda(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie serie;
        try {
            serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        } catch (ChequeraSerieException e) {
            return createDefaultDeudaChequera(facultadId, tipoChequeraId, chequeraSerieId);
        }

        Map<String, ChequeraPago> pagos = chequeraPagoService
                .findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, this)
                .stream()
                .collect(Collectors.toMap(ChequeraPago::getCuotaKey, Function.identity(), (pago, replacement) -> pago));

        var deudaInfo = repository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                        facultadId, tipoChequeraId, chequeraSerieId, serie.getAlternativaId(), 
                        (byte) 0, (byte) 0, Tool.hourAbsoluteArgentina(), BigDecimal.ZERO)
                .stream()
                .map(cuota -> {
                    BigDecimal deudaCuota = cuota.getImporte1()
                            .subtract(pagos.getOrDefault(cuota.cuotaKey(), new ChequeraPago()).getImporte())
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

        BigDecimal total = chequeraTotalService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId)
                .stream()
                .map(ChequeraTotal::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new DeudaChequera(
                serie.getPersonaId(), serie.getDocumentoId(), facultadId, "", 
                tipoChequeraId, "", chequeraSerieId, serie.getLectivoId(), "", 
                serie.getAlternativaId(), total, deudaInfo.getKey(), 
                deudaInfo.getValue(), serie.getChequeraId()
        );
    }

    public DeudaChequera calculateDeudaExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie serie = null;
        try {
            serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        } catch (ChequeraSerieException e) {
            return new DeudaChequera(BigDecimal.ZERO, 0, facultadId, "", tipoChequeraId, "", chequeraSerieId, 0, "", 1,
                    BigDecimal.ZERO, new BigDecimal(1000000), 1000, null);
        }
        Map<String, ChequeraPago> pagos = chequeraPagoService
                .findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, this).stream()
                .collect(Collectors.toMap(ChequeraPago::getCuotaKey, Function.identity(), (pago, replacement) -> pago));
        BigDecimal deuda = BigDecimal.ZERO;
        Integer cantidad = 0;
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
        return new DeudaChequera(serie.getPersonaId(), serie.getDocumentoId(), facultadId, facultad.getNombre(),
                tipoChequeraId, tipo.getNombre(), chequeraSerieId, serie.getLectivoId(), lectivo.getNombre(),
                serie.getAlternativaId(), total, deuda, cantidad, serie.getChequeraId());
    }

    public String calculateCodigoBarras(ChequeraCuota chequeraCuota) {
        String codigoBarras = "";

        if (chequeraCuota.getVencimiento3().isBefore(chequeraCuota.getVencimiento2())) {
            chequeraCuota.setVencimiento2(chequeraCuota.getVencimiento3());
        }
        if (chequeraCuota.getVencimiento2().isBefore(chequeraCuota.getVencimiento1())) {
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
        OffsetDateTime vencimiento1 = chequeraCuota.getVencimiento1().plusHours(3);
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

}
