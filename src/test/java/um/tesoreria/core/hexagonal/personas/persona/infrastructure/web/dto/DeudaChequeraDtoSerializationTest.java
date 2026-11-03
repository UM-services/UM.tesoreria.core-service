package um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class DeudaChequeraDtoSerializationTest {

    private final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void serializaLasDosGrafiasDeLosIdentificadores() {
        DeudaChequeraDto dto = new DeudaChequeraDto();
        dto.setTipochequeraId(3);
        dto.setChequeraserieId(7L);

        var node = mapper.readTree(mapper.writeValueAsString(dto));

        assertThat(node.get("tipochequeraId").asInt()).isEqualTo(3);
        assertThat(node.get("tipoChequeraId").asInt()).isEqualTo(3);
        assertThat(node.get("chequeraserieId").asLong()).isEqualTo(7L);
        assertThat(node.get("chequeraSerieId").asLong()).isEqualTo(7L);
    }
}
