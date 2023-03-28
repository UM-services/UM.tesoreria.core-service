package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import ar.edu.um.tesoreria.rest.repository.IFacturacionElectronicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FacturacionElectronicaService {

    @Autowired
    private IFacturacionElectronicaRepository repository;

    public FacturacionElectronica add(FacturacionElectronica facturacionElectronica) {
        facturacionElectronica = repository.save(facturacionElectronica);
        return facturacionElectronica;
    }
}
