package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.repository.MercadoPagoContextRepository;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class MercadoPagoContextService {

    private final MercadoPagoContextRepository repository;
    private final JsonMapper jsonMapper;

    public MercadoPagoContextService(MercadoPagoContextRepository repository) {
        this.repository = repository;
        this.jsonMapper = JsonMapper.builder()
                .findAndAddModules()
                .build();
    }

    public List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo) {
        return repository.findAllByChequeraCuotaIdAndActivo(chequeraCuotaId, activo);
    }

    public MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId)).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId", mercadoPagoContextId));
    }

    public MercadoPagoContext findActiveByChequeraCuotaId(Long chequeraCuotaId) {
        return Objects.requireNonNull(repository.findByChequeraCuotaIdAndActivo(chequeraCuotaId, (byte) 1)).orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para chequeraCuotaId", chequeraCuotaId));
    }

    public MercadoPagoContext add(MercadoPagoContext mercadoPagoContext) {
        return repository.save(mercadoPagoContext);
    }

    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> mercadoPagoContexts) {
        return repository.saveAll(mercadoPagoContexts);
    }

    private void logObject(String message, Object object) {
        if (log.isDebugEnabled()) {
            try {
                log.debug(message, jsonMapper.writerWithDefaultPrettyPrinter().writeValueAsString(object));
            } catch (JsonProcessingException e) {
                log.debug("Error serializing object: {}", e.getMessage());
            }
        }
    }

    public MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId) {
        if (mercadoPagoContextId == null || newMercadoPagoContext == null) {
            throw new MercadoPagoContextException("Invalid input parameters", mercadoPagoContextId);
        }

        log.debug("Updating MercadoPagoContext with id: {}", mercadoPagoContextId);
        logObject("New context data: {}", newMercadoPagoContext);

        return repository.findByMercadoPagoContextId(mercadoPagoContextId)
                .map(existingContext -> {
                    MercadoPagoContext updatedContext = copyProperties(newMercadoPagoContext, mercadoPagoContextId);
                    logObject("Before save: {}", updatedContext);
                    
                    MercadoPagoContext savedContext = repository.save(updatedContext);
                    logObject("After save: {}", savedContext);
                    
                    return savedContext;
                })
                .orElseThrow(() -> new MercadoPagoContextException("Context not found for id", mercadoPagoContextId));
    }

    private MercadoPagoContext copyProperties(MercadoPagoContext source, Long mercadoPagoContextId) {
        return MercadoPagoContext.builder()
                .mercadoPagoContextId(mercadoPagoContextId)
                .chequeraCuotaId(source.getChequeraCuotaId())
                .initPoint(source.getInitPoint())
                .fechaVencimiento(source.getFechaVencimiento())
                .importe(source.getImporte())
                .preference(source.getPreference())
                .activo(source.getActivo())
                .chequeraPagoId(source.getChequeraPagoId())
                .idMercadoPago(source.getIdMercadoPago())
                .status(source.getStatus())
                .fechaPago(source.getFechaPago())
                .fechaAcreditacion(source.getFechaAcreditacion())
                .importePagado(source.getImportePagado())
                .payment(source.getPayment())
                .build();
    }

}
