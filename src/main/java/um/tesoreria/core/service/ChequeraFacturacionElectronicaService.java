package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.exception.ChequeraFacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.ChequeraFacturacionElectronica;
import um.tesoreria.core.repository.ChequeraFacturacionElectronicaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ChequeraFacturacionElectronicaService {

    private final ChequeraFacturacionElectronicaRepository repository;

    public ChequeraFacturacionElectronicaService(ChequeraFacturacionElectronicaRepository repository) {
        this.repository = repository;
    }

    public ChequeraFacturacionElectronica findByChequeraId(Long chequeraId) {
        var chequeraFacturacionElectronica = repository.findByChequeraId(chequeraId).orElseThrow(() -> new ChequeraFacturacionElectronicaException(chequeraId, true));
        try {
            log.debug("ChequeraFacturacionElectronica -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(chequeraFacturacionElectronica));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraFacturacionElectronica -> {}", e.getMessage());
        }
        return chequeraFacturacionElectronica;
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
