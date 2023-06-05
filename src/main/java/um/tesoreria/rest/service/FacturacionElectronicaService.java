package um.tesoreria.rest.service;

import org.springframework.data.domain.Sort;
import um.tesoreria.rest.exception.FacturacionElectronicaException;
import um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import um.tesoreria.rest.repository.IFacturacionElectronicaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.rest.exception.FacturacionElectronicaException;
import um.tesoreria.rest.repository.IFacturacionElectronicaRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class FacturacionElectronicaService {

    @Autowired
    private IFacturacionElectronicaRepository repository;

    public List<FacturacionElectronica> findAllByPeriodo(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findAllByFechaReciboBetween(fechaDesde, fechaHasta, Sort.by("fechaRecibo").ascending().and(Sort.by("facturacionElectronicaId")));
    }

    public List<FacturacionElectronica> findAllByChequeraPagoIds(List<Long> chequeraPagoIds) {
        return repository.findAllByChequeraPagoIdIn(chequeraPagoIds);
    }

    public FacturacionElectronica findByFacturacionElectronicaId(Long facturacionElectronicaId) {
        return repository.findByFacturacionElectronicaId(facturacionElectronicaId).orElseThrow(() -> new FacturacionElectronicaException(facturacionElectronicaId));
    }

    public FacturacionElectronica add(FacturacionElectronica facturacionElectronica) {
        facturacionElectronica = repository.save(facturacionElectronica);
        return facturacionElectronica;
    }

}
