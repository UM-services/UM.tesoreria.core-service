/**
 *
 */
package um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.ChequeraCuotaException;
import um.tesoreria.rest.exception.ChequeraSerieException;
import um.tesoreria.rest.exception.FacultadException;
import um.tesoreria.rest.exception.LectivoException;
import um.tesoreria.rest.exception.TipoChequeraException;
import um.tesoreria.rest.kotlin.model.*;
import um.tesoreria.rest.model.ChequeraTotal;
import um.tesoreria.rest.model.dto.DeudaChequera;
import um.tesoreria.rest.repository.IChequeraCuotaRepository;
import um.tesoreria.rest.service.view.ChequeraCuotaDeudaService;
import um.tesoreria.rest.util.Tool;
import um.tesoreria.rest.exception.*;
import um.tesoreria.rest.repository.IChequeraCuotaRepository;

/**
 * @author daniel
 */
@Service
public class ChequeraCuotaService {

    @Autowired
    private IChequeraCuotaRepository repository;

    @Autowired
    private ChequeraSerieService chequeraSerieService;

    @Autowired
    private ChequeraPagoService chequeraPagoService;

    @Autowired
    private ChequeraTotalService chequeraTotalService;

    @Autowired
    private FacultadService facultadService;

    @Autowired
    private TipoChequeraService tipoChequeraService;

    @Autowired
    private LectivoService lectivoService;

    @Autowired
    private ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    @Transactional
    public List<ChequeraCuota> findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        repository.saveAll(repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId).stream().map(cuota -> {
            cuota.setChequeraId(chequeraSerie.getChequeraId());
            return cuota;
        }).toList());
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
                tipoChequeraId, chequeraSerieId, alternativaId, Sort.by("vencimiento1").ascending()
                        .and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending()))).stream().map(cuota -> {
            cuota.setChequeraSerie(chequeraSerie);
            return cuota;
        }).toList();
    }

    public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdConImporte(
            Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        repository.saveAll(repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId).stream().map(cuota -> {
            cuota.setChequeraId(chequeraSerie.getChequeraId());
            return cuota;
        }).toList());
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndImporte1GreaterThan(
                facultadId, tipoChequeraId, chequeraSerieId, alternativaId, BigDecimal.ZERO, Sort.by("vencimiento1")
                        .ascending().and(Sort.by("productoId").ascending().and(Sort.by("cuotaId").ascending()))).stream().map(cuota -> {
            cuota.setChequeraSerie(chequeraSerie);
            return cuota;
        }).toList();
    }

    public List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                              Integer alternativaId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        repository.saveAll(repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId).stream().map(cuota -> {
            cuota.setChequeraId(chequeraSerie.getChequeraId());
            return cuota;
        }).toList());
        return repository
                .findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
                        facultadId, tipoChequeraId, chequeraSerieId, alternativaId, (byte) 0, (byte) 0,
                        OffsetDateTime.now(), BigDecimal.ZERO).stream().map(cuota -> {
                    cuota.setChequeraSerie(chequeraSerie);
                    return cuota;
                }).toList();
    }

    public List<ChequeraCuota> findAllInconsistencias(OffsetDateTime desde, OffsetDateTime hasta) {
        List<ChequeraCuota> chequeraCuotas = new ArrayList<>();
        for (ChequeraCuota chequeraCuota : chequeraCuotaDeudaService.findAllByRango(desde, hasta, null).stream().map(cuotaDeuda -> cuotaDeuda.getChequeraCuota()).toList()) {
            if (chequeraCuota.getVencimiento1().isAfter(chequeraCuota.getVencimiento2())) {
                chequeraCuotas.add(chequeraCuota);
                continue;
            }
            if (chequeraCuota.getVencimiento2().isAfter(chequeraCuota.getVencimiento3())) {
                chequeraCuotas.add(chequeraCuota);
                continue;
            }
            if (chequeraCuota.getImporte1().compareTo(chequeraCuota.getImporte2()) > 0) {
                chequeraCuotas.add(chequeraCuota);
                continue;
            }
            if (chequeraCuota.getImporte2().compareTo(chequeraCuota.getImporte3()) > 0) {
                chequeraCuotas.add(chequeraCuota);
                continue;
            }
            if (chequeraCuota.getImporte1Original().multiply((new BigDecimal(49))).compareTo(chequeraCuota.getImporte1()) < 0) {
                chequeraCuotas.add(chequeraCuota);
                continue;
            }
            if (chequeraCuota.getImporte2Original().multiply((new BigDecimal(49))).compareTo(chequeraCuota.getImporte2()) < 0) {
                chequeraCuotas.add(chequeraCuota);
                continue;
            }
            if (chequeraCuota.getImporte3Original().multiply((new BigDecimal(49))).compareTo(chequeraCuota.getImporte3()) < 0) {
                chequeraCuotas.add(chequeraCuota);
            }
        }
        return chequeraCuotas;
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

    @Transactional
    public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipoChequeraId,
                                                                         Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId,
                chequeraSerieId);
    }

    public DeudaChequera calculateDeuda(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie serie = null;
        try {
            serie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        } catch (ChequeraSerieException e) {
            return new DeudaChequera(BigDecimal.ZERO, 0, facultadId, "", tipoChequeraId, "", chequeraSerieId, 0, "", 1,
                    BigDecimal.ZERO, new BigDecimal(1000000), 1000, null);
        }
        Map<String, ChequeraPago> pagos = chequeraPagoService
                .findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId).stream()
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
        return new DeudaChequera(serie.getPersonaId(), serie.getDocumentoId(), facultadId, "", tipoChequeraId, "",
                chequeraSerieId, serie.getLectivoId(), "", serie.getAlternativaId(), total, deuda, cantidad,
                serie.getChequeraId());
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
                .findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId).stream()
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
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getAnho() - 2000);
        // Cuota ID
        codigoBarras += new DecimalFormat("00").format(chequeraCuota.getCuotaId());
        // 1er Importe
        codigoBarras += new DecimalFormat("0000000").format(chequeraCuota.getImporte1());
        OffsetDateTime vencimiento1 = chequeraCuota.getVencimiento1().plusHours(3);
        OffsetDateTime vencimiento2 = chequeraCuota.getVencimiento2().plusHours(3);
        OffsetDateTime vencimiento3 = chequeraCuota.getVencimiento3().plusHours(3);
        // Dia 1er Vencimiento
        codigoBarras += new DecimalFormat("00").format(vencimiento1.getDayOfMonth());
        // Mes 1er Vencimiento
        codigoBarras += new DecimalFormat("00").format(vencimiento1.getMonthValue());
        // Año 1er Vencimiento
        codigoBarras += new DecimalFormat("0000").format(vencimiento1.getYear());
        // Dif 2do Importe
        codigoBarras += new DecimalFormat("00000")
                .format(chequeraCuota.getImporte2().subtract(chequeraCuota.getImporte1()).setScale(0));
        // Dif 2do Vencimiento
        Long diferencia1 = ChronoUnit.DAYS.between(vencimiento1.toLocalDate(), vencimiento2.toLocalDate());
        codigoBarras += new DecimalFormat("000").format(diferencia1);
        // Dif 3er Importe
        codigoBarras += new DecimalFormat("00000")
                .format(chequeraCuota.getImporte3().subtract(chequeraCuota.getImporte2()).setScale(0));
        // Dif 3er Vencimiento
        Long diferencia2 = ChronoUnit.DAYS.between(vencimiento2.toLocalDate(), vencimiento3.toLocalDate());
        codigoBarras += new DecimalFormat("000").format(diferencia2);
        // Codigo Verificador
        codigoBarras += Tool.calculateVerificadorRapiPago(codigoBarras);

        return codigoBarras;
    }

}
