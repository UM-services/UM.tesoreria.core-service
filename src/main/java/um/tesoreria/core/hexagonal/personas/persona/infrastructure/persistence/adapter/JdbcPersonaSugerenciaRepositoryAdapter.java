package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaSugerencia;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaSugerenciaRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcPersonaSugerenciaRepositoryAdapter implements PersonaSugerenciaRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public List<PersonaSugerencia> findByUsuarioAndTerms(Long userId, List<String> terms, int limite) {
        StringBuilder sql = new StringBuilder("""
                SELECT p.per_id, p.per_doc_id, d.Doc_Nombre, p.Per_Apellido, p.Per_Nombre
                FROM persona p
                JOIN documento d ON d.Doc_ID = p.per_doc_id
                WHERE EXISTS (
                    SELECT 1 FROM chequera_serie cs
                    JOIN usuario_chequera_facultad ucf ON ucf.facultad_id = cs.chs_fac_id
                    WHERE ucf.user_id = :userId
                      AND cs.chs_per_id = p.per_id AND cs.chs_doc_id = p.per_doc_id
                )
                """);
        var params = new MapSqlParameterSource("userId", userId).addValue("limite", limite);
        for (int i = 0; i < terms.size(); i++) {
            String key = "term" + i;
            sql.append(" AND (p.Per_Apellido LIKE :").append(key)
                    .append(" ESCAPE '!' OR p.Per_Nombre LIKE :").append(key).append(" ESCAPE '!')");
            params.addValue(key, "%" + escapeLike(terms.get(i)) + "%");
        }
        String firstTerm = escapeLike(terms.getFirst());
        params.addValue("prefix", firstTerm + "%");
        params.addValue("word", "% " + firstTerm + "%");
        sql.append("""
                 ORDER BY CASE
                    WHEN p.Per_Apellido LIKE :prefix ESCAPE '!' THEN 0
                    WHEN p.Per_Apellido LIKE :word ESCAPE '!' THEN 1
                    ELSE 2 END,
                  p.Per_Apellido, p.Per_Nombre, p.per_id, p.per_doc_id
                 LIMIT :limite
                """);
        return jdbc.query(sql.toString(), params, (rs, rowNum) -> new PersonaSugerencia(
                rs.getBigDecimal(1), rs.getInt(2), rs.getString(3), rs.getString(4), rs.getString(5)));
    }

    private static String escapeLike(String term) {
        return term.replace("!", "!!").replace("%", "!%").replace("_", "!_");
    }
}
