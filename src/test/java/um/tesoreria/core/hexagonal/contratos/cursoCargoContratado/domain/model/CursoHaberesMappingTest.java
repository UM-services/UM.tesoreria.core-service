package um.tesoreria.core.hexagonal.contratos.cursoCargoContratado.domain.model;

import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

import static org.assertj.core.api.Assertions.assertThat;

class CursoHaberesMappingTest {

    private final JsonMapper mapper = JsonMapper.builder().findAndAddModules().build();

    @Test
    void deserializaLosIdsQueDevuelveHaberes() {
        String json = """
                {"cursoId":10,"nombre":"Curso","facultadId":2,"geograficaId":3,"nivelId":4}
                """;

        CursoHaberes curso = mapper.readValue(json, CursoHaberes.class);

        assertThat(curso.getCursoId()).isEqualTo(10L);
        assertThat(curso.getFacultadId()).isEqualTo(2);
        assertThat(curso.getGeograficaId()).isEqualTo(3);
        assertThat(curso.getNivelId()).isEqualTo(4);
    }
}
