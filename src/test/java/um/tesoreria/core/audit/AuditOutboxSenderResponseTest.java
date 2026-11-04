package um.tesoreria.core.audit;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Contrato de la respuesta de {@code POST /evento/lote}: el lote sólo se puede dar por
 * entregado cuando la respuesta confirma qué pasó con cada fila.
 */
class AuditOutboxSenderResponseTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void interpretaUnaRespuestaCompleta() {
        AuditOutboxSender.BatchResponse response = AuditOutboxSender.BatchResponse.parse(objectMapper,
                "{\"accepted\":2,\"duplicates\":1,\"rejected\":[]}");

        assertThat(response.accepted()).isEqualTo(2);
        assertThat(response.duplicates()).isEqualTo(1);
        assertThat(response.rejected()).isEmpty();
        assertThat(response.covered()).isEqualTo(3);
    }

    @Test
    void interpretaLosRechazosPorIndice() {
        AuditOutboxSender.BatchResponse response = AuditOutboxSender.BatchResponse.parse(objectMapper,
                "{\"accepted\":1,\"duplicates\":0,\"rejected\":[{\"index\":1,\"reason\":\"entity es obligatorio\"}]}");

        assertThat(response.covered()).isEqualTo(2);
        assertThat(response.rejected()).containsExactly(Map.entry(1, "entity es obligatorio"));
    }

    @Test
    void unCuerpoVacioNoEsConfirmacion() {
        assertThatThrownBy(() -> AuditOutboxSender.BatchResponse.parse(objectMapper, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("vacío");
        assertThatThrownBy(() -> AuditOutboxSender.BatchResponse.parse(objectMapper, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unCuerpoQueNoEsJsonNoEsConfirmacion() {
        assertThatThrownBy(() -> AuditOutboxSender.BatchResponse.parse(objectMapper, "<html>ok</html>"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void unaRespuestaSinLosConteosNoEsConfirmacion() {
        assertThatThrownBy(() -> AuditOutboxSender.BatchResponse.parse(objectMapper, "{\"ok\":true}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("accepted/duplicates/rejected");
    }

    @Test
    void unaRespuestaConTiposInvalidosNoEsConfirmacion() {
        assertThatThrownBy(() -> AuditOutboxSender.BatchResponse.parse(objectMapper,
                "{\"accepted\":\"2\",\"duplicates\":0,\"rejected\":[]}"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void unRechazoSinIndiceNoEsConfirmacion() {
        assertThatThrownBy(() -> AuditOutboxSender.BatchResponse.parse(objectMapper,
                "{\"accepted\":1,\"duplicates\":0,\"rejected\":[{\"reason\":\"x\"}]}"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("index");
    }

    @Test
    void unaRespuestaParcialNoCubreElLote() {
        AuditOutboxSender.BatchResponse response = AuditOutboxSender.BatchResponse.parse(objectMapper,
                "{\"accepted\":1,\"duplicates\":0,\"rejected\":[]}");

        assertThat(response.covered()).isNotEqualTo(5);
    }
}
