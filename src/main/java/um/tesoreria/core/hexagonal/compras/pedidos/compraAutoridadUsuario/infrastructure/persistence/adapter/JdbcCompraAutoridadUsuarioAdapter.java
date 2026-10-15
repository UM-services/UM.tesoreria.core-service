package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;

import java.util.List;

/**
 * Persistencia de la tabla puente usuario ↔ perfil de autoridad.
 *
 * <p>Se resuelve con SQL directo (mismo criterio que la tabla puente de autorizantes de envío):
 * es una tabla de asociación con clave única compuesta y sin agregado propio.</p>
 */
@Component
@RequiredArgsConstructor
public class JdbcCompraAutoridadUsuarioAdapter implements CompraAutoridadUsuarioRepository {

    private static final String SELECT_PERFILES_SQL = """
            SELECT autoridad_perfil_id
            FROM compra_autoridad_usuario
            WHERE usuario_id = ?
            ORDER BY autoridad_perfil_id
            """;

    private static final String INSERT_SQL = """
            INSERT INTO compra_autoridad_usuario (usuario_id, autoridad_perfil_id, created)
            VALUES (?, ?, NOW(6))
            ON DUPLICATE KEY UPDATE updated = CURRENT_TIMESTAMP(6)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM compra_autoridad_usuario
            WHERE usuario_id = ? AND autoridad_perfil_id = ?
            """;

    private static final String EXISTS_SQL = """
            SELECT COUNT(*)
            FROM compra_autoridad_usuario
            WHERE usuario_id = ? AND autoridad_perfil_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Long> findPerfilIdsByUsuarioId(Integer usuarioId) {
        return jdbcTemplate.queryForList(SELECT_PERFILES_SQL, Long.class, usuarioId);
    }

    @Override
    public void asignar(Integer usuarioId, Long autoridadPerfilId) {
        jdbcTemplate.update(INSERT_SQL, usuarioId, autoridadPerfilId);
    }

    @Override
    public void quitar(Integer usuarioId, Long autoridadPerfilId) {
        jdbcTemplate.update(DELETE_SQL, usuarioId, autoridadPerfilId);
    }

    @Override
    public boolean existe(Integer usuarioId, Long autoridadPerfilId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_SQL, Integer.class, usuarioId, autoridadPerfilId);
        return count != null && count > 0;
    }

}
