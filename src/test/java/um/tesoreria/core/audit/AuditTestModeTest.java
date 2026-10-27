package um.tesoreria.core.audit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Los tests corren sobre H2 sin la tabla {@code audit_outbox}, así que la captura debe quedar
 * apagada. Si alguien cambia el default de {@code tesoreria.audit.mode} sin tocar la config de
 * test, esto falla antes de que los listeners rompan cada flush.
 */
@SpringBootTest
class AuditTestModeTest {

    @Autowired
    private AuditProperties properties;

    @Test
    void enTestsLaCapturaEstaApagada() {
        assertThat(properties.getMode()).isEqualTo(AuditProperties.Mode.OFF);
    }
}
