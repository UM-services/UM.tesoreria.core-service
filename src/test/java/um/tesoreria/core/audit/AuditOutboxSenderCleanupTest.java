package um.tesoreria.core.audit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import tools.jackson.databind.ObjectMapper;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Retención del outbox: {@code SENT} y {@code FAILED} se purgan, {@code PENDING} no
 * (son eventos no entregados). {@code FAILED} se mide por su fecha de fallo
 * ({@code next_attempt_at}), no por la fecha del evento.
 */
class AuditOutboxSenderCleanupTest {

    private static final String URL = "jdbc:h2:mem:audit_cleanup;DB_CLOSE_DELAY=-1";

    private DataSource dataSource;
    private AuditOutboxSender sender;

    @BeforeEach
    void setUp() throws Exception {
        dataSource = new DriverManagerDataSource(URL, "sa", "");
        AuditProperties properties = new AuditProperties();
        properties.setMode(AuditProperties.Mode.SEND);
        properties.setSentRetentionDays(7);
        properties.setFailedRetentionDays(30);
        properties.setPendingWarnHours(24);
        sender = new AuditOutboxSender(properties, dataSource, new ObjectMapper());

        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS audit_outbox");
            statement.executeUpdate("""
                    CREATE TABLE audit_outbox (
                        outbox_id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                        event_id        VARCHAR(36) NOT NULL,
                        payload         VARCHAR(4000) NOT NULL,
                        status          VARCHAR(12) NOT NULL,
                        attempts        INT NOT NULL DEFAULT 0,
                        next_attempt_at TIMESTAMP NOT NULL,
                        claim_token     VARCHAR(36),
                        created         TIMESTAMP NOT NULL,
                        sent_at         TIMESTAMP,
                        last_error      VARCHAR(500)
                    )""");
        }
    }

    @Test
    void purgaLasEntregadasViejasYConservaLasRecientes() throws Exception {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        insert("SENT", now.minusDays(10), now.minusDays(9), now.minusDays(9), 0);
        insert("SENT", now.minusDays(1), now.minusDays(1), now.minusDays(1), 0);

        sender.cleanup();

        assertThat(count("SENT")).isEqualTo(1);
        assertThat(countAll()).isEqualTo(1);
    }

    @Test
    void purgaLasFallidasSegunSuFechaDeFallo() throws Exception {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        // Falló hace 40 días -> se purga.
        insert("FAILED", now.minusDays(41), null, now.minusDays(40), 10);
        // Falló hace 2 días -> se conserva para inspección.
        insert("FAILED", now.minusDays(2), null, now.minusDays(2), 10);
        // Evento viejo que falló recién ayer: se conserva (se mide por el fallo, no por created).
        insert("FAILED", now.minusDays(90), null, now.minusDays(1), 10);

        sender.cleanup();

        assertThat(count("FAILED")).isEqualTo(2);
    }

    @Test
    void noPurgaLosPendientesAunqueSeanViejos() throws Exception {
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        insert("PENDING", now.minusDays(60), null, now.minusDays(60), 0);

        sender.cleanup();

        assertThat(count("PENDING")).isEqualTo(1);
        assertThat(countAll()).isEqualTo(1);
    }

    @Test
    void noFallaSiLaTablaNoExiste() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE audit_outbox");
        }

        sender.cleanup();
    }

    private void insert(String status, LocalDateTime created, LocalDateTime sentAt,
                        LocalDateTime nextAttemptAt, int attempts) throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement("""
                     INSERT INTO audit_outbox
                         (event_id, payload, status, attempts, next_attempt_at, created, sent_at)
                     VALUES (?, ?, ?, ?, ?, ?, ?)
                     """)) {
            statement.setString(1, java.util.UUID.randomUUID().toString());
            statement.setString(2, "{}");
            statement.setString(3, status);
            statement.setInt(4, attempts);
            statement.setTimestamp(5, Timestamp.valueOf(nextAttemptAt));
            statement.setTimestamp(6, Timestamp.valueOf(created));
            statement.setTimestamp(7, sentAt == null ? null : Timestamp.valueOf(sentAt));
            statement.executeUpdate();
        }
    }

    private long count(String status) throws Exception {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT COUNT(*) FROM audit_outbox WHERE status = ?")) {
            statement.setString(1, status);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
    }

    private long countAll() throws Exception {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM audit_outbox")) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}
