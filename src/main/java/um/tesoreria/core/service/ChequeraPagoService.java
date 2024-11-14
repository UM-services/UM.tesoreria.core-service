/**
 *
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import jakarta.transaction.Transactional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ChequeraPagoException;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.kotlin.model.FacturacionElectronica;
import um.tesoreria.core.repository.IChequeraPagoRepository;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ChequeraPagoService {

    private final IChequeraPagoRepository repository;
    private final FacturacionElectronicaService facturacionElectronicaService;

    public ChequeraPagoService(IChequeraPagoRepository repository, FacturacionElectronicaService facturacionElectronicaService) {
        this.repository = repository;
        this.facturacionElectronicaService = facturacionElectronicaService;
    }

    @Transactional
    public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, ChequeraCuotaService chequeraCuotaService) {
        List<ChequeraPago> pagos = repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);

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

        // Registrar chequeraPagos
        try {
            log.debug("ChequeraPagos: {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(pagos));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraPagos: {}", e.getMessage());
        }

        return pagos;
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
        List<ChequeraPago> pagos = repository.findAllByFechaAndTipoPagoIdGreaterThan(fechaPago, 2);

        // Map para asociar chequeraPagoId con ChequeraPago
        Map<Long, ChequeraPago> pagoMap = pagos.stream()
                .collect(Collectors.toMap(ChequeraPago::getChequeraPagoId, Function.identity()));

        // Rellena chequeraCuotaId si es nulo
        for (ChequeraPago pago : pagos) {
            if (pago.getChequeraCuotaId() == null) {
                pago.setChequeraCuotaId(chequeraCuotaService.findByUnique(
                        pago.getFacultadId(),
                        pago.getTipoChequeraId(),
                        pago.getChequeraSerieId(),
                        pago.getProductoId(),
                        pago.getAlternativaId(),
                        pago.getCuotaId()).getChequeraCuotaId());
            }
        }

        // Guarda todos los cambios en batch
        repository.saveAll(pagos);

        // Obtiene todas las facturaciones electrónicas asociadas a los pagos
        List<Long> pagoIds = new ArrayList<>(pagoMap.keySet());
        Map<Long, FacturacionElectronica> electronicas = facturacionElectronicaService
                .findAllByChequeraPagoIds(pagoIds)
                .stream()
                .collect(Collectors.toMap(FacturacionElectronica::getChequeraPagoId, Function.identity(), (pago, replacement) -> pago));

        // Filtra los pagos que no tienen facturación electrónica asociada
        return pagos.stream()
                .filter(pago -> !electronicas.containsKey(pago.getChequeraPagoId()))
                .filter(pago -> pago.getChequeraCuota() != null 
                    && pago.getChequeraCuota().getChequeraSerie() != null 
                    && pago.getChequeraCuota().getChequeraSerie().getPersona() != null)
                .collect(Collectors.toList());
    }

    public ChequeraPago findByChequeraPagoId(Long chequeraPagoId, ChequeraCuotaService chequeraCuotaService) {
        ChequeraPago chequeraPago = repository.findByChequeraPagoId(chequeraPagoId).orElseThrow(() -> new ChequeraPagoException(chequeraPagoId));
        if (chequeraPago.getChequeraCuotaId() == null) {
            chequeraPago.setChequeraCuotaId(chequeraCuotaService.findByUnique(chequeraPago.getFacultadId(), chequeraPago.getTipoChequeraId(), chequeraPago.getChequeraSerieId(), chequeraPago.getProductoId(), chequeraPago.getAlternativaId(), chequeraPago.getCuotaId()).getChequeraCuotaId());
            chequeraPago = repository.save(chequeraPago);
        }
        return chequeraPago;
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
                .orElse(1);
    }

}
