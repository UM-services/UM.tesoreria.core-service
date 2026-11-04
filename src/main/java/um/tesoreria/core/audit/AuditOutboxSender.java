package um.tesoreria.core.audit;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Toma las filas PENDING del outbox y las envía por lote al servicio de auditoría.
 *
 * <p>Sin SKIP LOCKED (MySQL 5.7): se reclaman con un token y un lease de 60 s. Si el
 * proceso muere, el lease vence y otro intento las retoma. La idempotencia real la
 * garantiza el {@code event_id} del lado de auditoría.</p>
 *
 * <p>Una fila sólo se marca {@code SENT} cuando la respuesta del servicio permite
 * confirmar qué pasó con <b>cada</b> evento del lote. Si el cuerpo viene vacío, no es
 * interpretable o no cubre todas las filas, el lote se reintenta: nunca se da por
 * entregado lo que no se puede verificar.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditOutboxSender {

    private static final String CLAIM = """
            UPDATE audit_outbox
               SET status = 'SENDING',
                   claim_token = ?,
                   next_attempt_at = ?
             WHERE status = 'PENDING'
                OR (status = 'SENDING' AND next_attempt_at <= ?)
             ORDER BY outbox_id
             LIMIT ?
            """;

    private static final String SELECT = """
            SELECT outbox_id, payload, attempts
              FROM audit_outbox
             WHERE claim_token = ?
             ORDER BY outbox_id
            """;

    private static final String PURGE_SENT = """
            DELETE FROM audit_outbox
             WHERE status = 'SENT'
               AND sent_at < ?
            """;

    /**
     * {@code FAILED} no tiene columna propia de fecha: al marcarla se deja
     * {@code next_attempt_at = ahora}, así que sirve como "momento en que falló".
     */
    private static final String PURGE_FAILED = """
            DELETE FROM audit_outbox
             WHERE status = 'FAILED'
               AND next_attempt_at < ?
            """;

    private static final String STUCK_PENDING = """
            SELECT COUNT(*), MIN(created)
              FROM audit_outbox
             WHERE status = 'PENDING'
               AND created < ?
            """;

    private final AuditProperties properties;
    private final DataSource dataSource;
    private final ObjectMapper objectMapper;

    private RestClient restClient;

    @PostConstruct
    void init() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofSeconds(5));
        factory.setReadTimeout(Duration.ofSeconds(15));
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Scheduled(fixedDelayString = "${tesoreria.audit.sender-delay-ms:2000}",
            initialDelayString = "${tesoreria.audit.sender-initial-delay-ms:5000}")
    public void sendPending() {
        if (properties.getMode() != AuditProperties.Mode.SEND) {
            return;
        }
        if (isBlank(properties.getUrl()) || isBlank(properties.getApiKey())) {
            log.warn("Auditoría: URL o API key no configuradas; el outbox queda pendiente");
            return;
        }

        String token = UUID.randomUUID().toString();
        List<OutboxRow> rows = claim(token, LocalDateTime.now(ZoneOffset.UTC));
        if (rows.isEmpty()) {
            return;
        }

        String body = rows.stream()
                .map(OutboxRow::payload)
                .collect(Collectors.joining(",", "{\"events\":[", "]}"));

        try {
            ResponseEntity<String> response = restClient.post()
                    .uri(properties.getUrl() + "/api/tesoreria/auditoria/evento/lote")
                    .header("X-API-Key", properties.getApiKey())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .toEntity(String.class);
            handleSuccess(rows, response.getBody());
        } catch (RestClientResponseException e) {
            int status = e.getStatusCode().value();
            if (status == 400 || status == 401 || status == 403 || status == 422) {
                markPermanentFailure(rows, "HTTP " + status + ": " + truncate(e.getResponseBodyAsString()));
            } else {
                retry(rows, "HTTP " + status + ": " + truncate(e.getResponseBodyAsString()));
            }
        } catch (RuntimeException e) {
            retry(rows, truncate(e.getClass().getSimpleName() + ": " + e.getMessage()));
        }
    }

    /**
     * Limpieza periódica del outbox. Corre cada hora (no una vez por día) para que un
     * reinicio del contenedor no saltee la ventana: se purgan las filas ya entregadas y
     * las fallidas, cada una con su propia retención.
     */
    @Scheduled(fixedDelayString = "${tesoreria.audit.cleanup-delay-ms:3600000}",
            initialDelayString = "${tesoreria.audit.cleanup-initial-delay-ms:60000}")
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        purge(PURGE_SENT, "SENT",
                now.minusDays(properties.getSentRetentionDays()), properties.getSentRetentionDays());
        purge(PURGE_FAILED, "FAILED",
                now.minusDays(properties.getFailedRetentionDays()), properties.getFailedRetentionDays());
        warnStuckPending(now);
    }

    private void purge(String sql, String label, LocalDateTime cutoff, int retentionDays) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setTimestamp(1, Timestamp.from(cutoff.toInstant(ZoneOffset.UTC)));
            int deleted = statement.executeUpdate();
            if (deleted > 0) {
                log.info("Auditoría: {} fila(s) {} eliminadas del outbox (retención {} días)",
                        deleted, label, retentionDays);
            }
        } catch (SQLException e) {
            log.warn("Auditoría: no se pudo limpiar el outbox ({}): {}", label, e.getMessage());
        }
    }

    /**
     * Un PENDING viejo significa que la entrega no está avanzando (URL/API key mal,
     * servicio caído o lote trabado). Se avisa, no se borra: son eventos no entregados.
     */
    private void warnStuckPending(LocalDateTime now) {
        LocalDateTime threshold = now.minusHours(properties.getPendingWarnHours());
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(STUCK_PENDING)) {
            statement.setTimestamp(1, Timestamp.from(threshold.toInstant(ZoneOffset.UTC)));
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    long count = resultSet.getLong(1);
                    Timestamp oldest = resultSet.getTimestamp(2);
                    if (count > 0) {
                        log.warn("Auditoría: {} evento(s) PENDING sin entregar hace más de {} h (el más viejo: {})",
                                count, properties.getPendingWarnHours(), oldest);
                    }
                }
            }
        } catch (SQLException e) {
            log.warn("Auditoría: no se pudo revisar el pendiente del outbox: {}", e.getMessage());
        }
    }

    private List<OutboxRow> claim(String token, LocalDateTime now) {
        List<OutboxRow> rows = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement claim = connection.prepareStatement(CLAIM)) {
                claim.setString(1, token);
                claim.setTimestamp(2, Timestamp.from(now.plusSeconds(60).toInstant(ZoneOffset.UTC)));
                claim.setTimestamp(3, Timestamp.from(now.toInstant(ZoneOffset.UTC)));
                claim.setInt(4, properties.getBatchSize());
                claim.executeUpdate();
            }
            try (PreparedStatement select = connection.prepareStatement(SELECT)) {
                select.setString(1, token);
                try (ResultSet resultSet = select.executeQuery()) {
                    while (resultSet.next()) {
                        rows.add(new OutboxRow(
                                resultSet.getLong("outbox_id"),
                                resultSet.getString("payload"),
                                resultSet.getInt("attempts")));
                    }
                }
            }
        } catch (SQLException e) {
            log.error("Auditoría: no se pudo reclamar el outbox: {}", e.getMessage());
            return List.of();
        }
        return rows;
    }

    private void handleSuccess(List<OutboxRow> rows, String responseBody) {
        BatchResponse response;
        try {
            response = BatchResponse.parse(objectMapper, responseBody);
        } catch (RuntimeException e) {
            retry(rows, "respuesta no interpretable del servicio de auditoría: " + truncate(e.getMessage()));
            return;
        }

        if (response.covered() != rows.size()) {
            retry(rows, "respuesta incompleta: confirma " + response.covered() + " de " + rows.size() + " evento(s)");
            return;
        }
        if (response.rejected().keySet().stream().anyMatch(index -> index < 0 || index >= rows.size())) {
            retry(rows, "respuesta inconsistente: índice de rechazo fuera del lote");
            return;
        }

        List<Long> sent = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            if (!response.rejected().containsKey(index)) {
                sent.add(rows.get(index).outboxId());
            }
        }
        markSent(sent);
        response.rejected().forEach((index, reason) ->
                markPermanentFailure(List.of(rows.get(index)), "rechazado: " + reason));
    }

    private void markSent(List<Long> outboxIds) {
        if (outboxIds.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE audit_outbox
                        SET status = 'SENT', sent_at = ?, claim_token = NULL, last_error = NULL
                      WHERE outbox_id = ?
                     """)) {
            for (Long outboxId : outboxIds) {
                statement.setTimestamp(1, Timestamp.from(now.toInstant(ZoneOffset.UTC)));
                statement.setLong(2, outboxId);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error("Auditoría: no se pudo marcar SENT: {}", e.getMessage());
        }
    }

    private void markPermanentFailure(List<OutboxRow> rows, String reason) {
        update(rows, "FAILED", reason, 0);
    }

    private void retry(List<OutboxRow> rows, String reason) {
        for (OutboxRow row : rows) {
            int attempts = row.attempts() + 1;
            if (attempts >= properties.getMaxAttempts()) {
                update(List.of(row), "FAILED", reason, 0);
            } else {
                int backoff = Math.min(60, 1 << Math.min(attempts, 6));
                update(List.of(row), "PENDING", reason, backoff);
            }
        }
    }

    private void update(List<OutboxRow> rows, String status, String reason, int backoffSeconds) {
        LocalDateTime nextAttempt = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(backoffSeconds);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     UPDATE audit_outbox
                        SET status = ?,
                            attempts = attempts + 1,
                            claim_token = NULL,
                            next_attempt_at = ?,
                            last_error = ?
                      WHERE outbox_id = ?
                     """)) {
            for (OutboxRow row : rows) {
                statement.setString(1, status);
                statement.setTimestamp(2, Timestamp.from(nextAttempt.toInstant(ZoneOffset.UTC)));
                statement.setString(3, truncate(reason));
                statement.setLong(4, row.outboxId());
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            log.error("Auditoría: no se pudo actualizar el outbox: {}", e.getMessage());
        }
    }

    private String truncate(String value) {
        if (value == null) {
            return null;
        }
        return value.length() <= 500 ? value : value.substring(0, 500);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private record OutboxRow(long outboxId, String payload, int attempts) {
    }

    /**
     * Respuesta de {@code POST /evento/lote}: {@code accepted + duplicates} quedaron
     * guardados y {@code rejected} trae los índices que no se aceptaron. Si el cuerpo no
     * permite calcular eso, no hay confirmación posible y el lote se reintenta.
     */
    record BatchResponse(int accepted, int duplicates, Map<Integer, String> rejected) {

        int covered() {
            return accepted + duplicates + rejected.size();
        }

        static BatchResponse parse(ObjectMapper objectMapper, String body) {
            if (body == null || body.isBlank()) {
                throw new IllegalArgumentException("cuerpo vacío");
            }
            JsonNode root = objectMapper.readTree(body);
            if (root == null || !root.isObject()) {
                throw new IllegalArgumentException("la respuesta no es un objeto JSON");
            }
            if (!root.has("accepted") || !root.has("duplicates") || !root.has("rejected")) {
                throw new IllegalArgumentException("faltan accepted/duplicates/rejected");
            }
            JsonNode acceptedNode = root.get("accepted");
            JsonNode duplicatesNode = root.get("duplicates");
            JsonNode rejectedNode = root.get("rejected");
            if (!acceptedNode.isNumber() || !duplicatesNode.isNumber() || !rejectedNode.isArray()) {
                throw new IllegalArgumentException("tipos inválidos en accepted/duplicates/rejected");
            }
            Map<Integer, String> rejected = new HashMap<>();
            for (JsonNode node : rejectedNode) {
                JsonNode indexNode = node.get("index");
                if (indexNode == null || !indexNode.isNumber()) {
                    throw new IllegalArgumentException("rechazo sin index");
                }
                rejected.put(indexNode.asInt(), node.has("reason") ? node.get("reason").asString() : "rejected");
            }
            return new BatchResponse(acceptedNode.asInt(), duplicatesNode.asInt(), rejected);
        }
    }
}
