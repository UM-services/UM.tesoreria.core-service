package um.tesoreria.core.hexagonal.chequera.chequeraSerie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("it")
@Transactional(readOnly = true)
class ChequerasPorUsuarioIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvcTester mockMvc;

    @Test
    void consultaUnAlumnoRealLimitadoALasFacultadesDelUsuario() {
        var rows = jdbc.queryForList("""
                SELECT ucf.user_id AS user_id, cs.chs_lec_id AS lectivo_id,
                       cs.chs_per_id AS persona_id, cs.chs_doc_id AS documento_id
                FROM usuario_chequera_facultad ucf
                JOIN chequera_serie cs ON cs.chs_fac_id = ucf.facultad_id
                WHERE cs.chs_lec_id IS NOT NULL AND cs.chs_per_id IS NOT NULL
                  AND cs.chs_doc_id IS NOT NULL
                LIMIT 1
                """);
        assertThat(rows).as("La base necesita una asignación con al menos una chequera").isNotEmpty();

        var sample = rows.getFirst();
        long userId = ((Number) sample.get("user_id")).longValue();
        int lectivoId = ((Number) sample.get("lectivo_id")).intValue();
        BigDecimal personaId = (BigDecimal) sample.get("persona_id");
        int documentoId = ((Number) sample.get("documento_id")).intValue();

        Long expected = jdbc.queryForObject("""
                SELECT COUNT(DISTINCT cs.clave)
                FROM chequera_serie cs
                JOIN usuario_chequera_facultad ucf ON ucf.facultad_id = cs.chs_fac_id
                WHERE ucf.user_id = ? AND cs.chs_lec_id = ?
                  AND cs.chs_per_id = ? AND cs.chs_doc_id = ?
                """, Long.class, userId, lectivoId, personaId, documentoId);
        assertThat(expected).isNotNull().isPositive();

        var result = mockMvc.get().uri("/api/tesoreria/core/chequeraSerie/usuario/" + userId
                        + "/lectivo/" + lectivoId + "?personaId=" + personaId.toPlainString()
                        + "&documentoId=" + documentoId + "&size=1")
                .assertThat().hasStatusOk().bodyJson();

        result.extractingPath("$.totalElements").isEqualTo(expected.intValue());
        result.extractingPath("$.content[0].documentoId").isNotNull();
        result.extractingPath("$.content[0].importeDeuda").isNotNull();
        result.extractingPath("$.content[0].estadoDeuda")
                .isIn("CON_DEUDA_VENCIDA", "SIN_DEUDA_VENCIDA");
    }
}
