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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Toma las filas PENDING del outbox y las envía por lote al servicio de auditoría.
 *
 * <p>Sin SKIP LOCKED (MySQL 5.7): se reclaman con un token y un lease de 60 s. Si el
 * proceso muere, el lease vence y otro intento las retoma. La idempotencia real la
 * garantiza el {@code event_id} del lado de auditoría.</p>
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

    @Scheduled(cron = "0 30 3 * * *")
    public void cleanup() {
        LocalDateTime cutoff = LocalDateTime.now(ZoneOffset.UTC).minusDays(7);
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "DELETE FROM audit_outbox WHERE status = 'SENT' AND sent_at < ?")) {
            statement.setTimestamp(1, Timestamp.from(cutoff.toInstant(ZoneOffset.UTC)));
            int deleted = statement.executeUpdate();
            if (deleted > 0) {
                log.info("Auditoría: {} fila(s) SENT eliminadas del outbox", deleted);
            }
        } catch (SQLException e) {
            log.warn("Auditoría: no se pudo limpiar el outbox: {}", e.getMessage());
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
        Set<Integer> rejected = new HashSet<>();
        Map<Integer, String> reasons = new HashMap<>();
        try {
            JsonNode root = responseBody == null ? null : objectMapper.readTree(responseBody);
            if (root != null && root.has("rejected")) {
                for (JsonNode node : root.get("rejected")) {
                    JsonNode indexNode = node.get("index");
                    if (indexNode == null || !indexNode.isNumber()) {
                        continue;
                    }
                    int index = indexNode.asInt();
                    if (index < 0 || index >= rows.size()) {
                        continue;
                    }
                    rejected.add(index);
                    reasons.put(index, node.has("reason") ? node.get("reason").asString() : "rejected");
                }
            }
        } catch (RuntimeException e) {
            log.warn("Auditoría: respuesta inesperada del servicio ({}); se marcan como enviadas", e.getMessage());
        }

        List<Long> sent = new ArrayList<>();
        for (int index = 0; index < rows.size(); index++) {
            if (!rejected.contains(index)) {
                sent.add(rows.get(index).outboxId());
            }
        }
        markSent(sent);
        rejected.forEach(index -> markPermanentFailure(
                List.of(rows.get(index)), "rechazado: " + reasons.getOrDefault(index, "sin motivo")));
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
}
