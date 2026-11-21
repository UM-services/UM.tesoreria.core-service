package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;
import um.tesoreria.core.service.facade.ToolService;

import java.util.List;
import java.util.Optional;

/**
 * Padrón de envío masivo en una sola consulta.
 *
 * <p>Parte de {@code vw_chequera_alta} —la especificación de «alta» del legacy: serie sin baja y
 * con clase de chequera 2 o 5— y la enriquece con los datos que la vista no trae: persona,
 * domicilio, tipo de chequera, tipo de arancel y el flag {@code retenida} de
 * {@code chequera_serie}. Es de sólo lectura: no hay entidad JPA ni escritura en este adaptador.</p>
 *
 * <p>Se usa {@link JdbcTemplate} (mismo patrón que {@code PedidoItemsPendientesJdbcAdapter}): una
 * proyección plana de reporte, sin N+1 y sin acoplar el slice a entidades de otros slices. Medido
 * contra {@code tesium}: 1.612 filas en ~130 ms para el lectivo 37.</p>
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JdbcChequeraEnvioAltaReaderAdapter implements ChequeraEnvioAltaReader {

    private static final String SELECT = """
            SELECT v.clave              AS chequera_id,
                   v.ChS_Fac_ID         AS facultad_id,
                   v.ChS_TCh_ID         AS tipo_chequera_id,
                   v.ChS_ID             AS chequera_serie_id,
                   v.ChS_Alt_ID         AS alternativa_id,
                   v.ChS_Per_ID         AS persona_id,
                   v.ChS_Doc_ID         AS documento_id,
                   v.ChS_Lec_ID         AS lectivo_id,
                   v.ChS_Geo_ID         AS geografica_id,
                   v.enviado            AS enviado,
                   cs.retenida          AS retenida,
                   p.Per_Apellido       AS apellido,
                   p.Per_Nombre         AS nombre,
                   t.tch_nombre         AS tipo_chequera,
                   a.art_descripcion    AS arancel_tipo,
                   d.Dom_e_mail         AS email_personal,
                   d.mail_institucional AS email_institucional
            FROM vw_chequera_alta v
            JOIN persona p  ON p.per_id = v.ChS_Per_ID AND p.per_doc_id = v.ChS_Doc_ID
            JOIN tipo_chequera t ON t.tch_id = v.ChS_TCh_ID
            LEFT JOIN arancel_tipo a ON a.art_id = v.ChS_ArT_ID
            JOIN chequera_serie cs ON cs.ChS_Fac_ID = v.ChS_Fac_ID
                                  AND cs.ChS_TCh_ID = v.ChS_TCh_ID
                                  AND cs.ChS_ID = v.ChS_ID
            LEFT JOIN domicilio d ON d.dom_per_id = v.ChS_Per_ID AND d.dom_doc_id = v.ChS_Doc_ID
            """;

    private static final String SQL_POR_FILTRO = SELECT
            + " WHERE v.ChS_Lec_ID = ? AND v.ChS_Fac_ID = ? AND v.ChS_Geo_ID = ?"
            + " ORDER BY p.Per_Apellido, p.Per_Nombre, v.ChS_ID";

    private static final String SQL_POR_CHEQUERA = SELECT + " WHERE v.clave = ?";

    private final JdbcTemplate jdbcTemplate;

    /**
     * Se reutiliza la validación del resto del sistema en lugar de duplicar el criterio: el flag
     * de «correo válido» decide si la serie se envía, se retiene o se omite (§4.6 del plan).
     */
    private final ToolService toolService;

    @Override
    public List<ChequeraEnvioAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId,
                                                                                 Integer facultadId,
                                                                                 Integer geograficaId) {
        List<ChequeraEnvioAlta> altas = jdbcTemplate.query(SQL_POR_FILTRO, rowMapper(),
                lectivoId, facultadId, geograficaId);
        log.debug("Envío masivo: {} series para lectivo {} facultad {} sede {}",
                altas.size(), lectivoId, facultadId, geograficaId);
        return altas;
    }

    @Override
    public Optional<ChequeraEnvioAlta> findByChequeraId(Long chequeraId) {
        return jdbcTemplate.query(SQL_POR_CHEQUERA, rowMapper(), chequeraId).stream().findFirst();
    }

    private RowMapper<ChequeraEnvioAlta> rowMapper() {
        return (rs, rowNum) -> {
            String emailPersonal = rs.getString("email_personal");
            String emailInstitucional = rs.getString("email_institucional");
            return new ChequeraEnvioAlta(
                    rs.getLong("chequera_id"),
                    rs.getInt("facultad_id"),
                    rs.getInt("tipo_chequera_id"),
                    rs.getLong("chequera_serie_id"),
                    rs.getInt("alternativa_id"),
                    rs.getBigDecimal("persona_id"),
                    rs.getInt("documento_id"),
                    rs.getInt("lectivo_id"),
                    rs.getInt("geografica_id"),
                    rs.getString("apellido"),
                    rs.getString("nombre"),
                    rs.getString("tipo_chequera"),
                    rs.getString("arancel_tipo"),
                    emailPersonal,
                    emailInstitucional,
                    esValido(emailPersonal),
                    esValido(emailInstitucional),
                    rs.getBoolean("enviado"),
                    rs.getBoolean("retenida"));
        };
    }

    private boolean esValido(String email) {
        return email != null && !email.isBlank() && Boolean.TRUE.equals(toolService.mailValidate(email.trim()));
    }
}
