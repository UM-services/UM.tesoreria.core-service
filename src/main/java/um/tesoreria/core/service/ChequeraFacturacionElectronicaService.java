package um.tesoreria.core.service;

import um.tesoreria.core.exception.ChequeraFacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.ChequeraFacturacionElectronica;
import um.tesoreria.core.repository.IChequeraFacturacionElectronicaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChequeraFacturacionElectronicaService {

    @Autowired
    private IChequeraFacturacionElectronicaRepository repository;

    public ChequeraFacturacionElectronica findByChequeraId(Long chequeraId) {
        return repository.findByChequeraId(chequeraId).orElseThrow(() -> new ChequeraFacturacionElectronicaException(chequeraId, true));
    }

    public ChequeraFacturacionElectronica add(ChequeraFacturacionElectronica chequeraFacturacionElectronica) {
        return repository.save(chequeraFacturacionElectronica);
    }

    public ChequeraFacturacionElectronica update(ChequeraFacturacionElectronica newChequeraFacturacionElectronica, Long chequeraFacturacionElectronicaId) {
        return repository.findByChequeraFacturacionElectronicaId(chequeraFacturacionElectronicaId).map(chequeraFacturacionElectronica -> {
            chequeraFacturacionElectronica = new ChequeraFacturacionElectronica(chequeraFacturacionElectronicaId, newChequeraFacturacionElectronica.getChequeraId(), newChequeraFacturacionElectronica.getCuit(), newChequeraFacturacionElectronica.getRazonSocial(), newChequeraFacturacionElectronica.getDomicilio(), newChequeraFacturacionElectronica.getEmail(), newChequeraFacturacionElectronica.getCondicionIva());
            chequeraFacturacionElectronica = repository.save(chequeraFacturacionElectronica);
            return chequeraFacturacionElectronica;
        }).orElseThrow(() -> new ChequeraFacturacionElectronicaException(chequeraFacturacionElectronicaId));
    }

    @Transactional
    public void deleteByChequeraFacturacionElectronicaId(Long chequeraFacturacionElectronicaId) {
        repository.deleteByChequeraFacturacionElectronicaId(chequeraFacturacionElectronicaId);
    }
}
