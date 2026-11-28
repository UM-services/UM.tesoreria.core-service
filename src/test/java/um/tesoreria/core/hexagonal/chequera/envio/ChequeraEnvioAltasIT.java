package um.tesoreria.core.hexagonal.chequera.envio;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Contraste del padrón de envío migrado a Criteria contra el SQL anterior (vista + joins) usado
 * como oráculo: mismos registros, mismos campos y mismo orden.
 */
@SpringBootTest
@ActiveProfiles("it")
@Transactional(readOnly = true)
class ChequeraEnvioAltasIT {

    /** El SELECT del adaptador JDBC anterior, tal cual, como referencia. */
    private static final String SQL_ANTERIOR = """
            SELECT v.clave AS chequera_id, v.ChS_Fac_ID AS facultad_id, v.ChS_TCh_ID AS tipo_chequera_id,
                   v.ChS_ID AS chequera_serie_id, v.ChS_Alt_ID AS alternativa_id, v.ChS_Per_ID AS persona_id,
                   v.ChS_Doc_ID AS documento_id, v.ChS_Lec_ID AS lectivo_id, v.ChS_Geo_ID AS geografica_id,
                   v.enviado AS enviado,
                   p.Per_Apellido AS apellido, p.Per_Nombre AS nombre, t.tch_nombre AS tipo_chequera,
                   a.art_descripcion AS arancel_tipo, d.Dom_e_mail AS email_personal,
                   d.mail_institucional AS email_institucional
              FROM vw_chequera_alta v
              JOIN persona p ON p.per_id = v.ChS_Per_ID AND p.per_doc_id = v.ChS_Doc_ID
              JOIN tipo_chequera t ON t.tch_id = v.ChS_TCh_ID
              LEFT JOIN arancel_tipo a ON a.art_id = v.ChS_ArT_ID
              LEFT JOIN domicilio d ON d.dom_per_id = v.ChS_Per_ID AND d.dom_doc_id = v.ChS_Doc_ID
             WHERE v.ChS_Lec_ID = ? AND v.ChS_Fac_ID = ? AND v.ChS_Geo_ID = ?
             ORDER BY p.Per_Apellido, p.Per_Nombre, v.ChS_ID
            """;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private ChequeraEnvioAltaReader reader;

    @Test
    void elPadronCoincideConElSqlAnterior() {
        Map<String, Object> combo = jdbc.queryForMap("""
                SELECT ChS_Lec_ID AS lectivo, ChS_Fac_ID AS facultad, ChS_Geo_ID AS sede
                FROM vw_chequera_alta GROUP BY 1, 2, 3 ORDER BY COUNT(*) DESC LIMIT 1
                """);
        Integer lectivo = ((Number) combo.get("lectivo")).intValue();
        Integer facultad = ((Number) combo.get("facultad")).intValue();
        Integer sede = ((Number) combo.get("sede")).intValue();

        List<ChequeraEnvioAlta> obtenido = reader
                .findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivo, facultad, sede);
        List<Map<String, Object>> esperado = jdbc.queryForList(SQL_ANTERIOR, lectivo, facultad, sede);

        assertThat(obtenido).as("filas del padrón").hasSameSizeAs(esperado);
        assertThat(obtenido).isNotEmpty();

        Map<Long, Map<String, Object>> porClave = new LinkedHashMap<>();
        esperado.forEach(fila -> porClave.put(((Number) fila.get("chequera_id")).longValue(), fila));
        for (ChequeraEnvioAlta alta : obtenido) {
            Map<String, Object> fila = porClave.get(alta.chequeraId());
            assertThat(fila).as("serie %s en el SQL de referencia", alta.chequeraId()).isNotNull();
            assertThat(alta.facultadId()).isEqualTo(((Number) fila.get("facultad_id")).intValue());
            assertThat(alta.tipoChequeraId()).isEqualTo(((Number) fila.get("tipo_chequera_id")).intValue());
            assertThat(alta.chequeraSerieId()).isEqualTo(((Number) fila.get("chequera_serie_id")).longValue());
            assertThat(alta.documentoId()).isEqualTo(((Number) fila.get("documento_id")).intValue());
            assertThat(alta.lectivoId()).isEqualTo(((Number) fila.get("lectivo_id")).intValue());
            assertThat(alta.geograficaId()).isEqualTo(((Number) fila.get("geografica_id")).intValue());
            assertThat(alta.apellido()).isEqualTo(fila.get("apellido"));
            assertThat(alta.nombre()).isEqualTo(fila.get("nombre"));
            assertThat(alta.tipoChequera()).isEqualTo(fila.get("tipo_chequera"));
            assertThat(alta.arancelTipo()).isEqualTo(fila.get("arancel_tipo"));
            assertThat(alta.emailPersonal()).isEqualTo(fila.get("email_personal"));
            assertThat(alta.emailInstitucional()).isEqualTo(fila.get("email_institucional"));
            assertThat(alta.enviado()).isEqualTo(booleano(fila.get("enviado")));
        }
        // Mismo orden: la primera fila del SQL es la primera del padrón.
        assertThat(obtenido.getFirst().chequeraId())
                .isEqualTo(((Number) esperado.getFirst().get("chequera_id")).longValue());
    }

    @Test
    void porClaveDevuelveLaMismaFila() {        Long clave = jdbc.queryForList("SELECT clave FROM vw_chequera_alta ORDER BY clave LIMIT 1", Long.class)
                .stream().findFirst().orElse(null);
        assumeTrue(clave != null, "La base de este entorno no tiene series de alta");

        ChequeraEnvioAlta alta = reader.findByChequeraId(clave).orElseThrow();
        Map<String, Object> fila = jdbc.queryForMap("""
                SELECT v.clave AS chequera_id, v.ChS_Lec_ID AS lectivo_id, p.Per_Apellido AS apellido
                  FROM vw_chequera_alta v
                  JOIN persona p ON p.per_id = v.ChS_Per_ID AND p.per_doc_id = v.ChS_Doc_ID
                 WHERE v.clave = ?
                """, clave);

        assertThat(alta.chequeraId()).isEqualTo(((Number) fila.get("chequera_id")).longValue());
        assertThat(alta.lectivoId()).isEqualTo(((Number) fila.get("lectivo_id")).intValue());
        assertThat(alta.apellido()).isEqualTo(fila.get("apellido"));

        Long inexistente = jdbc.queryForObject("SELECT COALESCE(MAX(clave), 0) + 1 FROM vw_chequera_alta", Long.class);
        assertThat(reader.findByChequeraId(inexistente)).isEmpty();
    }

    /** {@code tinyint(1)} llega como {@link Boolean} o como número según el driver. */
    private static boolean booleano(Object valor) {
        if (valor instanceof Boolean bandera) {
            return bandera;
        }
        return valor instanceof Number numero && numero.intValue() != 0;
    }

}
