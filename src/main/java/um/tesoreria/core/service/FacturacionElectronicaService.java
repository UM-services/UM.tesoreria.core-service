package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import um.tesoreria.core.exception.FacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.FacturacionElectronica;
import um.tesoreria.core.repository.FacturacionElectronicaRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@Slf4j
public class FacturacionElectronicaService {

    private final FacturacionElectronicaRepository repository;

    public FacturacionElectronicaService(FacturacionElectronicaRepository repository) {
        this.repository = repository;
    }

    public List<FacturacionElectronica> findAllByPeriodo(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta) {
        return repository.findAllByFechaReciboBetween(fechaDesde, fechaHasta, Sort.by("fechaRecibo").ascending().and(Sort.by("facturacionElectronicaId")));
    }

    public List<FacturacionElectronica> findAllByChequeraPagoIds(List<Long> chequeraPagoIds) {
        var facturacionElectronicas = repository.findAllByChequeraPagoIdIn(chequeraPagoIds);
        try {
            log.debug("FacturacionElectronicas: {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(facturacionElectronicas));
        } catch (JsonProcessingException e) {
            log.debug("FacturacionElectronicas: {}", e.getMessage());
        }
        return facturacionElectronicas;
    }

    public List<FacturacionElectronica> find3Pendientes() {
        return repository.findTop3ByEnviadaAndRetriesLessThanOrderByFacturacionElectronicaIdDesc((byte) 0, 3);
    }

    public FacturacionElectronica findByFacturacionElectronicaId(Long facturacionElectronicaId) {
        var facturacionElectronica = repository.findByFacturacionElectronicaId(facturacionElectronicaId).orElseThrow(() -> new FacturacionElectronicaException("facturacionElectronicaId", facturacionElectronicaId));
        try {
            log.debug("FacturacionElectronica: {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(facturacionElectronica));
        } catch (JsonProcessingException e) {
            log.debug("FacturacionElectronica: {}", e.getMessage());
        }
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
        return repository.findByFacturacionElectronicaId(facturacionElectronicaId).map(facturacionElectronica -> {
            facturacionElectronica = new FacturacionElectronica.Builder()
                    .facturacionElectronicaId(facturacionElectronicaId)
                    .chequeraPagoId(newFacturacionElectronica.getChequeraPagoId())
                    .comprobanteId(newFacturacionElectronica.getComprobanteId())
                    .numeroComprobante(newFacturacionElectronica.getNumeroComprobante())
                    .personaId(newFacturacionElectronica.getPersonaId())
                    .tipoDocumento(newFacturacionElectronica.getTipoDocumento())
                    .apellido(newFacturacionElectronica.getApellido())
                    .nombre(newFacturacionElectronica.getNombre())
                    .cuit(newFacturacionElectronica.getCuit())
                    .condicionIva(newFacturacionElectronica.getCondicionIva())
                    .importe(newFacturacionElectronica.getImporte())
                    .cae(newFacturacionElectronica.getCae())
                    .fechaRecibo(newFacturacionElectronica.getFechaRecibo())
                    .fechaVencimientoCae(newFacturacionElectronica.getFechaVencimientoCae())
                    .enviada(newFacturacionElectronica.getEnviada())
                    .retries(newFacturacionElectronica.getRetries())
                    .build();
            return repository.save(facturacionElectronica);
        }).orElseThrow(() ->new FacturacionElectronicaException("facturacionElectronicaId", facturacionElectronicaId));
    }

}
