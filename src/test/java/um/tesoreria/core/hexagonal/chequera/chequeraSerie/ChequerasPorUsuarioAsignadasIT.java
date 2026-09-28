package um.tesoreria.core.hexagonal.chequera.chequeraSerie;

import org.junit.jupiter.api.Assumptions;
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
class ChequerasPorUsuarioAsignadasIT {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvcTester mockMvc;

    @Test
    void consultaUnAlumnoRealLimitadoALasTresAsignacionesDelUsuario() {
        var rows = jdbc.queryForList("""
                SELECT ucf.user_id AS user_id, cs.chs_lec_id AS lectivo_id,
                       cs.chs_per_id AS persona_id, cs.chs_doc_id AS documento_id
                FROM usuario_chequera_facultad ucf
                JOIN usuario_chequera_geografica ucg ON ucg.user_id = ucf.user_id
                JOIN usuario_chequera_clase_chequera ucc ON ucc.user_id = ucf.user_id
                JOIN chequera_serie cs ON cs.chs_fac_id = ucf.facultad_id
                                       AND cs.chs_geo_id = ucg.geografica_id
                JOIN tipo_chequera tc ON tc.tch_id = cs.chs_tch_id
                                      AND tc.tch_cch_id = ucc.clase_chequera_id
                WHERE cs.chs_lec_id IS NOT NULL AND cs.chs_per_id IS NOT NULL
                  AND cs.chs_doc_id IS NOT NULL
                LIMIT 1
                """);
        Assumptions.assumeTrue(!rows.isEmpty(),
                "La base IT no tiene un usuario con facultad, sede y clase asignadas con chequeras");

        var sample = rows.getFirst();
        long userId = ((Number) sample.get("user_id")).longValue();
        int lectivoId = ((Number) sample.get("lectivo_id")).intValue();
        BigDecimal personaId = (BigDecimal) sample.get("persona_id");
        int documentoId = ((Number) sample.get("documento_id")).intValue();

        Long expected = jdbc.queryForObject("""
                SELECT COUNT(DISTINCT cs.clave)
                FROM chequera_serie cs
                JOIN usuario_chequera_facultad ucf ON ucf.user_id = ? AND ucf.facultad_id = cs.chs_fac_id
                JOIN usuario_chequera_geografica ucg ON ucg.user_id = ? AND ucg.geografica_id = cs.chs_geo_id
                JOIN tipo_chequera tc ON tc.tch_id = cs.chs_tch_id
                JOIN usuario_chequera_clase_chequera ucc ON ucc.user_id = ?
                                                       AND ucc.clase_chequera_id = tc.tch_cch_id
                WHERE cs.chs_lec_id = ? AND cs.chs_per_id = ? AND cs.chs_doc_id = ?
                """, Long.class, userId, userId, userId, lectivoId, personaId, documentoId);
        assertThat(expected).isNotNull().isPositive();

        var result = mockMvc.get().uri("/api/tesoreria/core/chequeraSerie/usuario/" + userId
                        + "/lectivo/" + lectivoId + "/asignaciones?personaId=" + personaId.toPlainString()
                        + "&documentoId=" + documentoId + "&size=1")
                .assertThat().hasStatusOk().bodyJson();

        result.extractingPath("$.totalElements").isEqualTo(expected.intValue());
        result.extractingPath("$.content[0].documentoId").isNotNull();
        result.extractingPath("$.content[0].geograficaId").isNotNull();
        result.extractingPath("$.content[0].estadoDeuda")
                .isIn("CON_DEUDA_VENCIDA", "SIN_DEUDA_VENCIDA");
    }

    @Test
    void usuarioConFacultadPeroSinSedeAsignadaNoVeNada() {
        var rows = jdbc.queryForList("""
                SELECT ucf.user_id AS user_id, cs.chs_lec_id AS lectivo_id
                FROM usuario_chequera_facultad ucf
                JOIN chequera_serie cs ON cs.chs_fac_id = ucf.facultad_id
                WHERE cs.chs_lec_id IS NOT NULL
                  AND NOT EXISTS (SELECT 1 FROM usuario_chequera_geografica ucg
                                  WHERE ucg.user_id = ucf.user_id)
                LIMIT 1
                """);
        Assumptions.assumeTrue(!rows.isEmpty(),
                "La base IT no tiene un usuario con facultades pero sin sedes asignadas");

        var sample = rows.getFirst();
        long userId = ((Number) sample.get("user_id")).longValue();
        int lectivoId = ((Number) sample.get("lectivo_id")).intValue();

        var result = mockMvc.get().uri("/api/tesoreria/core/chequeraSerie/usuario/" + userId
                        + "/lectivo/" + lectivoId + "/asignaciones?size=20")
                .assertThat().hasStatusOk().bodyJson();

        result.extractingPath("$.content").asList().isEmpty();
    }
}
