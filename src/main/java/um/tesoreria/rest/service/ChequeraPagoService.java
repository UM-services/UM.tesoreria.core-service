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
 *
 */
@Service
public class ChequeraPagoService {

    @Autowired
    private IChequeraPagoRepository repository;

    @Autowired
    private FacturacionElectronicaService facturacionElectronicaService;

    public List<ChequeraPago> findAllByChequera(Integer facultadId, Integer tipochequeraId, Long chequeraserieId) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId,
                chequeraserieId);
    }

    public List<ChequeraPago> pendientesFactura(OffsetDateTime fechaPago) {
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
        return repository.findByChequeraPagoId(chequeraPagoId)
                .orElseThrow(() -> new ChequeraPagoException(chequeraPagoId));
    }

    @Transactional
    public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipochequeraId,
                                                                         Long chequeraserieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipochequeraId,
                chequeraserieId);
    }

    public ChequeraPago add(ChequeraPago chequeraPago) {
        chequeraPago = repository.save(chequeraPago);
        return chequeraPago;
    }

}
