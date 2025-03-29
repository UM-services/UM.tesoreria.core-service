package um.tesoreria.core.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraMessageCheckException;
import um.tesoreria.core.kotlin.repository.ChequeraMessageCheckRepository;
import um.tesoreria.core.model.ChequeraMessageCheck;

import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class ChequeraMessageCheckService {

    private final ChequeraMessageCheckRepository repository;

    public ChequeraMessageCheckService(ChequeraMessageCheckRepository repository) {
        this.repository = repository;
    }

    public ChequeraMessageCheck findByChequeraMessageCheckId(UUID chequeraMessageCheckId) {
        log.debug("Processing ChequeraMessageCheckService.findByChequeraMessageCheckId");
        ChequeraMessageCheck chequeraMessageCheck = Objects.requireNonNull(repository.findByChequeraMessageCheckId(chequeraMessageCheckId))
                .orElseThrow(() -> new ChequeraMessageCheckException(chequeraMessageCheckId));
        logChequeraMessageCheck(chequeraMessageCheck);
        return chequeraMessageCheck;
    }

    public ChequeraMessageCheck add(ChequeraMessageCheck chequeraMessageCheck) {
        log.debug("Processing ChequeraMessageCheckService.add");
        chequeraMessageCheck = repository.save(chequeraMessageCheck);
        logChequeraMessageCheck(chequeraMessageCheck);
        return chequeraMessageCheck;
    }

    private void logChequeraMessageCheck(ChequeraMessageCheck chequeraMessageCheck) {
        try {
            log.debug("ChequeraMessageCheck -> {}", JsonMapper
                    .builder()
                    .findAndAddModules()
                    .build()
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(chequeraMessageCheck));
        } catch (JsonProcessingException e) {
            log.debug("ChequeraMessageCheck jsonify error: {}", e.getMessage());
        }
    }

}
