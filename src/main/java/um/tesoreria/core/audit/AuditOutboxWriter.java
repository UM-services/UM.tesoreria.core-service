package um.tesoreria.core.audit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import tools.jackson.databind.ObjectMapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Escribe el outbox usando la conexión de la transacción en curso: o se confirman
 * el cambio de negocio y su evento, o ninguno.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class AuditOutboxWriter {

    private static final String INSERT = """
            INSERT INTO audit_outbox
                (event_id, payload, status, attempts, next_attempt_at, created)
            VALUES (?, ?, 'PENDING', 0, ?, ?)
            """;

    @PersistenceContext
    private EntityManager entityManager;

    private final PlatformTransactionManager transactionManager;
    private final ObjectMapper objectMapper;

    public void write(SharedSessionContractImplementor session, List<AuditEventPayload> events) {
        if (events.isEmpty()) {
            return;
        }
        session.doWork(connection -> insert(connection, events));
    }

    /**
     * Para el evento REQUEST, que se emite cuando la transacción de negocio ya
     * terminó (incluso si hizo rollback).
     */
    public void writeInNewTransaction(List<AuditEventPayload> events) {
        if (events.isEmpty()) {
            return;
        }
        TransactionTemplate template = new TransactionTemplate(transactionManager);
        template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        template.executeWithoutResult(status -> {
            Session session = entityManager.unwrap(Session.class);
            session.doWork(connection -> insert(connection, events));
        });
    }

    private void insert(Connection connection, List<AuditEventPayload> events) throws SQLException {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
            for (AuditEventPayload event : events) {
                statement.setString(1, event.getEventId());
                statement.setString(2, objectMapper.writeValueAsString(event));
                statement.setTimestamp(3, Timestamp.from(now.toInstant(ZoneOffset.UTC)));
                statement.setTimestamp(4, Timestamp.from(now.toInstant(ZoneOffset.UTC)));
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
