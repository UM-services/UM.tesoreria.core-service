package um.tesoreria.core.service.queue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import um.tesoreria.core.configuration.RabbitMQConfig;
import um.tesoreria.core.kotlin.model.dto.message.ChequeraMessageDto;

@Service
@Slf4j
public class ChequeraQueueService {

    private final RabbitTemplate rabbitTemplate;

    public ChequeraQueueService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendChequeraQueue(ChequeraMessageDto chequeraMessage) {
        log.debug("Processing ChequeraQueueService.sendChequeraQueue");
        log.debug("Encolando envío");
        logChequeraMessage(chequeraMessage);
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_CHEQUERA, chequeraMessage);
    }

    private void logChequeraMessage(ChequeraMessageDto chequeraMessage) {
        try {
            log.debug("ChequeraMessage -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(chequeraMessage));
        } catch (JsonProcessingException e) {
            log.error("ChequeraMessage jsonify error -> {}", e.getMessage());
        }
    }

}
