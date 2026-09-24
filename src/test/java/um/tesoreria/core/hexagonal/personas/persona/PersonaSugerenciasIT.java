package um.tesoreria.core.hexagonal.personas.persona;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaSugerenciaService;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Transactional(readOnly = true)
class PersonaSugerenciasIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private PersonaSugerenciaService sugerenciaService;

    @Test
    void sugiereSoloPersonasDeFacultadesAsignadasYSinCamposSensibles() {
        var rows = jdbc.queryForList("""
                SELECT ucf.user_id, p.per_id, p.per_doc_id, p.Per_Apellido, p.Per_Nombre
                FROM usuario_chequera_facultad ucf
                JOIN chequera_serie cs ON cs.chs_fac_id = ucf.facultad_id
                JOIN persona p ON p.per_id = cs.chs_per_id AND p.per_doc_id = cs.chs_doc_id
                WHERE CHAR_LENGTH(TRIM(p.Per_Apellido)) >= 3
                  AND CHAR_LENGTH(TRIM(p.Per_Nombre)) >= 3
                LIMIT 1
                """);
        assertThat(rows).as("La base necesita una persona con chequera en una facultad asignada").isNotEmpty();

        var sample = rows.getFirst();
        long userId = ((Number) sample.get("user_id")).longValue();
        String q = (sample.get("Per_Apellido") + " " + sample.get("Per_Nombre"));
        var uri = URI.create("/api/tesoreria/core/persona/sugerencias/usuario/" + userId
                + "?q=" + URLEncoder.encode(q, StandardCharsets.UTF_8).replace("+", "%20") + "&limite=20");
        var result = mockMvc.get().uri(uri)
                .assertThat().hasStatusOk().bodyJson();

        result.extractingPath("$").asArray().isNotEmpty();
        result.extractingPath("$[0]").asMap()
                .containsOnlyKeys("personaId", "documentoId", "documento", "apellido", "nombre");
        result.extractingPath("$[0].personaId").isNotNull();
        result.extractingPath("$[0].documentoId").isNotNull();

        var suggestions = sugerenciaService.findByUsuario(userId, q, 20);
        assertThat(suggestions).isNotEmpty().allSatisfy(person -> {
            Long assignments = jdbc.queryForObject("""
                    SELECT COUNT(*) FROM chequera_serie cs
                    JOIN usuario_chequera_facultad ucf ON ucf.facultad_id = cs.chs_fac_id
                    WHERE ucf.user_id = ? AND cs.chs_per_id = ? AND cs.chs_doc_id = ?
                    """, Long.class, userId, person.personaId(), person.documentoId());
            assertThat(assignments).isPositive();
        });

        long withoutFacultades = jdbc.queryForObject("SELECT COALESCE(MAX(user_id), 0) + 1 FROM usuario_chequera_facultad", Long.class);
        mockMvc.get().uri("/api/tesoreria/core/persona/sugerencias/usuario/" + withoutFacultades + "?q=abc")
                .assertThat().hasStatusOk().bodyJson().isEqualTo("[]");
        mockMvc.get().uri("/api/tesoreria/core/persona/sugerencias/usuario/" + userId + "?q=ab")
                .assertThat().hasStatus(400);
        mockMvc.get().uri("/api/tesoreria/core/persona/sugerencias/usuario/" + userId + "?q=abc&limite=21")
                .assertThat().hasStatus(400);
    }
}
