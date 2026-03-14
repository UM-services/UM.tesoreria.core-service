package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import um.tesoreria.core.exception.FacturacionElectronicaException;
import um.tesoreria.core.model.FacturacionElectronica;
import um.tesoreria.core.repository.FacturacionElectronicaRepository;
import org.springframework.stereotype.Service;
import um.tesoreria.core.util.Jsonifier;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FacturacionElectronicaService {

    private final FacturacionElectronicaRepository repository;

    public List<FacturacionElectronica> findAllByPeriodo(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findAllByFechaReciboBetween(fechaDesde, fechaHasta, Sort.by("fechaRecibo").ascending().and(Sort.by("facturacionElectronicaId")));
    }

    public List<FacturacionElectronica> findAllByChequeraPagoIds(List<Long> chequeraPagoIds) {
        var facturacionElectronicas = repository.findAllByChequeraPagoIdIn(chequeraPagoIds);
        log.debug("FacturacionElectronicas: {}", Jsonifier.builder(facturacionElectronicas).build());
        return facturacionElectronicas;
    }

    public List<FacturacionElectronica> find3Pendientes() {
        return repository.findTop10ByEnviadaAndRetriesLessThanOrderByFacturacionElectronicaIdDesc((byte) 0, 3);
    }

    public FacturacionElectronica findByFacturacionElectronicaId(Long facturacionElectronicaId) {
        var facturacionElectronica = repository.findByFacturacionElectronicaId(facturacionElectronicaId).orElseThrow(() -> new FacturacionElectronicaException("facturacionElectronicaId", facturacionElectronicaId));
        log.debug("FacturacionElectronica: {}", facturacionElectronica.jsonify());
        return facturacionElectronica;
    }

    public FacturacionElectronica findByChequeraPagoId(Long chequeraPagoId) {
        return repository.findByChequeraPagoId(chequeraPagoId).orElseThrow(() -> new FacturacionElectronicaException("chequeraPagoId", chequeraPagoId));
    }

    public FacturacionElectronica add(FacturacionElectronica facturacionElectronica) {
        facturacionElectronica = repository.save(facturacionElectronica);
        return facturacionElectronica;
    }

    public FacturacionElectronica update(FacturacionElectronica newFacturacionElectronica, Long facturacionElectronicaId) {
        log.debug("Processing FacturacionElectronicaService.update");
        return repository.findByFacturacionElectronicaId(facturacionElectronicaId).map(facturacionElectronica -> {
            facturacionElectronica.setChequeraPagoId(newFacturacionElectronica.getChequeraPagoId());
            facturacionElectronica.setComprobanteId(newFacturacionElectronica.getComprobanteId());
            facturacionElectronica.setNumeroComprobante(newFacturacionElectronica.getNumeroComprobante());
            facturacionElectronica.setPersonaId(newFacturacionElectronica.getPersonaId());
            facturacionElectronica.setTipoDocumento(newFacturacionElectronica.getTipoDocumento());
            facturacionElectronica.setApellido(newFacturacionElectronica.getApellido());
            facturacionElectronica.setNombre(newFacturacionElectronica.getNombre());
            facturacionElectronica.setCuit(newFacturacionElectronica.getCuit());
            facturacionElectronica.setCondicionIva(newFacturacionElectronica.getCondicionIva());
            facturacionElectronica.setImporte(newFacturacionElectronica.getImporte());
            facturacionElectronica.setCae(newFacturacionElectronica.getCae());
            facturacionElectronica.setFechaRecibo(newFacturacionElectronica.getFechaRecibo());
            facturacionElectronica.setFechaVencimientoCae(newFacturacionElectronica.getFechaVencimientoCae());
            facturacionElectronica.setEnviada(newFacturacionElectronica.getEnviada());
            facturacionElectronica.setRetries(newFacturacionElectronica.getRetries());
            facturacionElectronica = repository.save(facturacionElectronica);
            log.debug("FacturacionElectronica -> {}", facturacionElectronica.jsonify());
            return facturacionElectronica;
        }).orElseThrow(() ->new FacturacionElectronicaException("facturacionElectronicaId", facturacionElectronicaId));
    }

}
