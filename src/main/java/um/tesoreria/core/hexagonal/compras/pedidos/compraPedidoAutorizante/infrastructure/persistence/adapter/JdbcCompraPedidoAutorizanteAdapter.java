package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.domain.ports.out.CompraPedidoAutorizanteRepository;

import java.util.List;

/**
 * Persistencia de la tabla puente autorizante ↔ dependencia.
 *
 * <p>Se resuelve con SQL directo (mismo criterio que el correlativo y el ejercicio de este
 * subdominio): es una tabla de asociación con clave compuesta y sin agregado propio.</p>
 */
@Component
@RequiredArgsConstructor
public class JdbcCompraPedidoAutorizanteAdapter implements CompraPedidoAutorizanteRepository {

    private static final String SELECT_DEPENDENCIAS_SQL = """
            SELECT dependencia_id
            FROM compra_pedido_autorizante_dependencia
            WHERE autorizante_id = ?
            ORDER BY dependencia_id
            """;

    private static final String INSERT_SQL = """
            INSERT INTO compra_pedido_autorizante_dependencia (autorizante_id, dependencia_id, created)
            VALUES (?, ?, NOW(6))
            ON DUPLICATE KEY UPDATE updated = CURRENT_TIMESTAMP(6)
            """;

    private static final String DELETE_SQL = """
            DELETE FROM compra_pedido_autorizante_dependencia
            WHERE autorizante_id = ? AND dependencia_id = ?
            """;

    private static final String EXISTS_SQL = """
            SELECT COUNT(*)
            FROM compra_pedido_autorizante_dependencia
            WHERE autorizante_id = ? AND dependencia_id = ?
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Integer> findDependenciaIdsByAutorizanteId(Integer autorizanteId) {
        return jdbcTemplate.queryForList(SELECT_DEPENDENCIAS_SQL, Integer.class, autorizanteId);
    }

    @Override
    public void asignar(Integer autorizanteId, Integer dependenciaId) {
        jdbcTemplate.update(INSERT_SQL, autorizanteId, dependenciaId);
    }

    @Override
    public void quitar(Integer autorizanteId, Integer dependenciaId) {
        jdbcTemplate.update(DELETE_SQL, autorizanteId, dependenciaId);
    }

    @Override
    public boolean existe(Integer autorizanteId, Integer dependenciaId) {
        Integer count = jdbcTemplate.queryForObject(EXISTS_SQL, Integer.class, autorizanteId, dependenciaId);
        return count != null && count > 0;
    }

}
