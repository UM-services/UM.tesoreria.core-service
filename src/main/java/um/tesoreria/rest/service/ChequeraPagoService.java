/**
 *
 */
package um.tesoreria.rest.service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.ChequeraPagoException;
import um.tesoreria.rest.kotlin.model.ChequeraPago;
import um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import um.tesoreria.rest.repository.IChequeraPagoRepository;
import um.tesoreria.rest.exception.ChequeraPagoException;
import um.tesoreria.rest.repository.IChequeraPagoRepository;

/**
 * @author daniel
 */
@Service
public class ChequeraPagoService {

    @Autowired
    private IChequeraPagoRepository repository;

    @Autowired
    private FacturacionElectronicaService facturacionElectronicaService;

    @Autowired
    private ChequeraCuotaService chequeraCuotaService;

    @Transactional
    public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        repository.saveAll(repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId).stream().map(pago -> {
            if (pago.getChequeraCuotaId() == null) {
                pago.setChequeraCuotaId(chequeraCuotaService.findByUnique(pago.getFacultadId(), pago.getTipoChequeraId(), pago.getChequeraSerieId(), pago.getProductoId(), pago.getAlternativaId(), pago.getCuotaId()).getChequeraCuotaId());
            }
            return pago;
        }).toList());
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }

    @Transactional
    public List<ChequeraPago> pendientesFactura(OffsetDateTime fechaPago) {
        repository.saveAll(repository.findAllByFechaAndTipoPagoIdGreaterThan(fechaPago, 2).stream().map(pago -> {
            if (pago.getChequeraCuotaId() == null) {
                pago.setChequeraCuotaId(chequeraCuotaService.findByUnique(pago.getFacultadId(), pago.getTipoChequeraId(), pago.getChequeraSerieId(), pago.getProductoId(), pago.getAlternativaId(), pago.getCuotaId()).getChequeraCuotaId());
            }
            return pago;
        }).toList());
        List<ChequeraPago> pagos = repository.findAllByFechaAndTipoPagoIdGreaterThan(fechaPago, 2);
        List<Long> pagoIds = pagos.stream().map(pago -> pago.getChequeraPagoId()).collect(Collectors.toList());
        Map<Long, FacturacionElectronica> electronicas = facturacionElectronicaService.findAllByChequeraPagoIds(pagoIds).stream().collect(Collectors.toMap(FacturacionElectronica::getChequeraPagoId, Function.identity(), (pago, replacement) -> pago));
        List<ChequeraPago> chequeraPagos = new ArrayList<ChequeraPago>();
        for (ChequeraPago pago : pagos) {
            if (!electronicas.containsKey(pago.getChequeraPagoId())) {
                chequeraPagos.add(pago);
            }
        }
        return chequeraPagos;
    }

    public ChequeraPago findByChequeraPagoId(Long chequeraPagoId) {
        ChequeraPago chequeraPago = null;
        chequeraPago = repository.findByChequeraPagoId(chequeraPagoId).orElseThrow(() -> new ChequeraPagoException(chequeraPagoId));
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

    public ChequeraPago add(ChequeraPago chequeraPago) {
        if (chequeraPago.getChequeraCuotaId() == null) {
            chequeraPago.setChequeraCuotaId(chequeraCuotaService.findByUnique(chequeraPago.getFacultadId(), chequeraPago.getTipoChequeraId(), chequeraPago.getChequeraSerieId(), chequeraPago.getProductoId(), chequeraPago.getAlternativaId(), chequeraPago.getCuotaId()).getChequeraCuotaId());
        }
        chequeraPago = repository.save(chequeraPago);
        return chequeraPago;
    }

}
