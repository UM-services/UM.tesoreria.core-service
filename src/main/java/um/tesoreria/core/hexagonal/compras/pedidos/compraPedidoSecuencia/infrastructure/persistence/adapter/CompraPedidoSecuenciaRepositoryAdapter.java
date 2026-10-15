package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoSecuencia.domain.ports.out.CompraPedidoSecuenciaRepository;

/**
 * Reserva atómica del correlativo anual.
 *
 * <p>Usa {@code LAST_INSERT_ID(expr)} de MySQL: la primera reserva del ejercicio inserta
 * {@code ultimo_numero = 1} y las siguientes incrementan en la misma sentencia, de modo que
 * dos alta concurrentes del mismo ejercicio no pueden repetir número. La reserva vive en la
 * transacción del alta.</p>
 */
@Component
@RequiredArgsConstructor
public class CompraPedidoSecuenciaRepositoryAdapter implements CompraPedidoSecuenciaRepository {

    private static final String RESERVAR_SQL = """
            INSERT INTO compra_pedido_secuencia (ejercicio_id, anio, ultimo_numero, created)
            VALUES (?, ?, LAST_INSERT_ID(1), NOW(6))
            ON DUPLICATE KEY UPDATE ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1), updated = CURRENT_TIMESTAMP(6)
            """;

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int reservarSiguienteNumero(Integer ejercicioId, Integer anio) {
        jdbcTemplate.update(RESERVAR_SQL, ejercicioId, anio);
        Integer numero = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        return numero == null ? 0 : numero;
    }

}
