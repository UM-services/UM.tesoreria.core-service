package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MateriasEstadoResponseRoundTripTest {

    private static final String FIXTURE = "/materiasInscriptas/materias-control.json";

    private final JsonMapper mapper = JsonMapper.builder().build();

    // protects=los DTOs de materias representan sin pérdida el contrato publicado (nombres, enums y anidado);
    // fails_when=un campo, un enum o un nivel del contrato no existe en los DTOs, o cambia de nombre al serializar;
    // no_claim=que el core arme este contenido (en #482 la lista siempre es vacía), ni el formato del texto: se compara como árbol JSON.
    @Test
    void materiasControlFixtureSurvivesTheRoundTripThroughTheDtos() throws IOException {
        JsonNode original;
        try (InputStream in = getClass().getResourceAsStream(FIXTURE)) {
            assertThat(in).as("fixture %s", FIXTURE).isNotNull();
            original = mapper.readTree(in);
        }

        List<MateriasEstadoResponse> dtos = mapper.readerFor(new TypeReference<List<MateriasEstadoResponse>>() { })
                .readValue(original);
        JsonNode roundTrip = mapper.valueToTree(dtos);

        assertThat(dtos).hasSize(1);
        assertThat(roundTrip).isEqualTo(original);
    }
}
