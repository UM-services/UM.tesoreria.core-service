/**
 *
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraPagoException;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.kotlin.model.FacturacionElectronica;
import um.tesoreria.core.repository.ChequeraPagoRepository;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ChequeraPagoService {

    private static final int TIPO_PAGO_THRESHOLD = 2;

    private final ChequeraPagoRepository repository;
    private final FacturacionElectronicaService facturacionElectronicaService;
    private final JsonMapper jsonMapper;

    public ChequeraPagoService(ChequeraPagoRepository repository, FacturacionElectronicaService facturacionElectronicaService) {
        this.repository = repository;
        this.facturacionElectronicaService = facturacionElectronicaService;
        this.jsonMapper = JsonMapper.builder().findAndAddModules().build();
    }

    private void fillChequeraCuotaId(ChequeraPago pago, ChequeraCuotaService chequeraCuotaService) {
        if (pago.getChequeraCuotaId() == null) {
            pago.setChequeraCuotaId(chequeraCuotaService.findByUnique(
                pago.getFacultadId(),
                pago.getTipoChequeraId(),
                pago.getChequeraSerieId(),
                pago.getProductoId(),
                pago.getAlternativaId(),
                pago.getCuotaId()
            ).getChequeraCuotaId());
        }
    }

    private List<ChequeraPago> processPagos(List<ChequeraPago> pagos, ChequeraCuotaService chequeraCuotaService) {
        pagos.forEach(pago -> fillChequeraCuotaId(pago, chequeraCuotaService));
        return repository.saveAll(pagos);
    }

    public List<ChequeraPago> findAllByTipoPagoIdAndFechaAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion) {
        return repository.findAllByTipoPagoIdAndAcreditacion(tipoPagoId, fechaAcreditacion);
    }

    public List<ChequeraPago> findAllByTipoPagoIdAndFechaPago(Integer tipoPagoId, OffsetDateTime fechaPago) {
        // Crear fecha inicio (00:00:00) y fecha fin (23:59:59.999999999)
        OffsetDateTime fechaInicio = fechaPago.withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fechaFin = fechaPago.withHour(23).withMinute(59).withSecond(59).withNano(999999999);

        return repository.findAllByTipoPagoIdAndFechaBetween(tipoPagoId, fechaInicio, fechaFin);
    }

    @Transactional
    public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, ChequeraCuotaService chequeraCuotaService) {
        List<ChequeraPago> pagos = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);

        return processPagos(pagos, chequeraCuotaService);
    }

    @Transactional
    public List<ChequeraPago> findAllByCuota(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, ChequeraCuotaService chequeraCuotaService) {
        List<ChequeraPago> pagos = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);

        // Rellena chequeraCuotaId si es nulo y guarda los cambios
        pagos.forEach(pago -> {
            if (pago.getChequeraCuotaId() == null) {
                pago.setChequeraCuotaId(chequeraCuotaService.findByUnique(
                        pago.getFacultadId(),
                        pago.getTipoChequeraId(),
                        pago.getChequeraSerieId(),
                        pago.getProductoId(),
                        pago.getAlternativaId(),
                        pago.getCuotaId()).getChequeraCuotaId());
            }
        });

        // Guarda todos los cambios en batch
        repository.saveAll(pagos);

        return pagos;
    }

    @Transactional
    public List<ChequeraPago> pendientesFactura(OffsetDateTime fechaPago, ChequeraCuotaService chequeraCuotaService) {
        // Crear fecha inicio (00:00:00) y fecha fin (23:59:59.999999999)
        OffsetDateTime fechaInicio = fechaPago.withHour(0).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fechaFin = fechaPago.withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        
        List<ChequeraPago> pagos = repository.findAllByFechaBetweenAndTipoPagoIdGreaterThan(fechaInicio, fechaFin, TIPO_PAGO_THRESHOLD);
        processPagos(pagos, chequeraCuotaService);

        List<Long> pagoIds = pagos.stream()
            .map(ChequeraPago::getChequeraPagoId)
            .collect(Collectors.toList());

        Map<Long, FacturacionElectronica> electronicas = facturacionElectronicaService
            .findAllByChequeraPagoIds(pagoIds)
            .stream()
            .collect(Collectors.toMap(
                FacturacionElectronica::getChequeraPagoId,
                Function.identity(),
                (existing, replacement) -> existing
            ));

        return pagos.stream()
            .filter(pago -> !electronicas.containsKey(pago.getChequeraPagoId()))
            .filter(this::isValidChequeraPago)
            .collect(Collectors.toList());
    }

    public List<ChequeraPago> findAllByFacultadIdAndTipoChequeraIdAndLectivoId(Integer facultadId, Integer tipoChequeraId, Integer lectivoId) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraCuotaChequeraSerieLectivoId(facultadId, tipoChequeraId, lectivoId);
    }

    private boolean isValidChequeraPago(ChequeraPago pago) {
        return Optional.ofNullable(pago.getChequeraCuota())
            .map(ChequeraCuota::getChequeraSerie)
            .map(ChequeraSerie::getPersona)
            .isPresent();
    }

    public ChequeraPago findByChequeraPagoId(Long chequeraPagoId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraPago chequeraPago = repository.findByChequeraPagoId(chequeraPagoId).orElseThrow(() -> new ChequeraPagoException(chequeraPagoId));
        if (chequeraPago.getChequeraCuotaId() == null) {
            chequeraPago.setChequeraCuotaId(chequeraCuotaService.findByUnique(chequeraPago.getFacultadId(), chequeraPago.getTipoChequeraId(), chequeraPago.getChequeraSerieId(), chequeraPago.getProductoId(), chequeraPago.getAlternativaId(), chequeraPago.getCuotaId()).getChequeraCuotaId());
            chequeraPago = repository.save(chequeraPago);
        }
        return chequeraPago;
    }

    public ChequeraPago findByIdMercadoPago(String idMercadoPago) {
        log.debug("Processing ChequeraPagoService.findByIdMercadoPago -> {}", idMercadoPago);
        return repository.findByIdMercadoPago(idMercadoPago).orElseThrow(() -> new ChequeraPagoException(idMercadoPago));
    }

    @Transactional
    public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }

    public ChequeraPago add(ChequeraPago chequeraPago, ChequeraCuotaService chequeraCuotaService) {
        if (chequeraPago.getChequeraCuotaId() == null) {
            chequeraPago.setChequeraCuotaId(chequeraCuotaService.findByUnique(chequeraPago.getFacultadId(), chequeraPago.getTipoChequeraId(), chequeraPago.getChequeraSerieId(), chequeraPago.getProductoId(), chequeraPago.getAlternativaId(), chequeraPago.getCuotaId()).getChequeraCuotaId());
        }
        chequeraPago = repository.save(chequeraPago);
        return chequeraPago;
    }

    public boolean isPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId, ChequeraCuotaService chequeraCuotaService) {
        return !findAllByCuota(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, chequeraCuotaService).isEmpty();
    }

    public Integer nextOrden(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        log.debug("Processing nextOrden");
        return repository.findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdOrderByOrdenDesc(
                facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId)
                .map(pago -> pago.getOrden() + 1)
                .orElse(0);
    }

}
