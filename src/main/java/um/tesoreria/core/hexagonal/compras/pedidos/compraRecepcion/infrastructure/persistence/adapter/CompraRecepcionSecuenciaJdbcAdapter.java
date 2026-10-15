package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionSecuenciaPort;

/**
 * Reserva atómica del correlativo anual de recepciones ({@code RC-AAAA-NNNNNN}) con
 * {@code LAST_INSERT_ID} (mismo mecanismo que {@code compra_pedido_secuencia} /
 * {@code compra_formalizacion_secuencia}).
 */
@Component
@RequiredArgsConstructor
public class CompraRecepcionSecuenciaJdbcAdapter implements CompraRecepcionSecuenciaPort {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public int reservarNumero(Integer ejercicioId, Integer anio) {
        jdbcTemplate.update(
                "INSERT INTO compra_recepcion_secuencia (ejercicio_id, anio, ultimo_numero, created, updated) "
                        + "VALUES (?, ?, LAST_INSERT_ID(1), NOW(6), NOW(6)) "
                        + "ON DUPLICATE KEY UPDATE ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1)",
                ejercicioId, anio);
        Integer ultimo = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        return ultimo == null ? 0 : ultimo;
    }

}
