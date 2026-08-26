package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ReciboMessageCheckException;
import um.tesoreria.core.kotlin.repository.ReciboMessageCheckRepository;
import um.tesoreria.core.model.ReciboMessageCheck;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ReciboMessageCheckService {

    private final ReciboMessageCheckRepository repository;

    public ReciboMessageCheckService(ReciboMessageCheckRepository repository) {
        this.repository = repository;
    }

    public ReciboMessageCheck findByReciboMessageCheckId(UUID reciboMessageCheckId) {
        log.debug("Processing ReciboMessageCheckService.findByReciboMessageCheckId");
        ReciboMessageCheck reciboMessageCheck = Objects.requireNonNull(repository.findByReciboMessageCheckId(reciboMessageCheckId))
                .orElseThrow(() -> new ReciboMessageCheckException(reciboMessageCheckId));
        logReciboMessageCheck(reciboMessageCheck);
        return reciboMessageCheck;
    }

    /// //NUEVOOOO////
    public List<ReciboMessageCheck> findAllByChequeraPagoId(Long chequeraPagoId) {
        log.debug("Processing ReciboMessageCheckService.findAllByChequeraPagoId");
        return repository.findAllByChequeraPagoId(chequeraPagoId);
    }

    public ReciboMessageCheck add(ReciboMessageCheck reciboMessageCheck) {
        log.debug("Processing ReciboMessageCheckService.add");
        reciboMessageCheck = repository.save(reciboMessageCheck);
        logReciboMessageCheck(reciboMessageCheck);
        return reciboMessageCheck;
    }

    /// //NUEVOOOO////
    public ReciboMessageCheck update(ReciboMessageCheck reciboMessageCheck) {
        log.debug("Processing ReciboMessageCheckService.update");
        reciboMessageCheck = repository.save(reciboMessageCheck);
        logReciboMessageCheck(reciboMessageCheck);
        return reciboMessageCheck;
    }

    private void logReciboMessageCheck(ReciboMessageCheck reciboMessageCheck) {
        try {
            log.debug("ReciboMessageCheck -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(reciboMessageCheck));
        } catch (JsonProcessingException e) {
            log.debug("ReciboMessageCheck jsonify error: {}", e.getMessage());
        }
    }

}