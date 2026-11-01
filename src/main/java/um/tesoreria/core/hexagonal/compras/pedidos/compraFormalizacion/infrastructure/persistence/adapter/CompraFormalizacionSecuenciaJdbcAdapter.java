package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import um.tesoreria.core.audit.AuditManualChange;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.out.CompraFormalizacionSecuenciaPort;

/**
 * Reserva atómica del correlativo anual de Órdenes de Compra ({@code OC-AAAA-NNNNNN}) con
 * {@code LAST_INSERT_ID} (mismo mecanismo que {@code compra_pedido_secuencia} /
 * {@code compra_cotizacion_secuencia}).
 */
@Component
@RequiredArgsConstructor
public class CompraFormalizacionSecuenciaJdbcAdapter implements CompraFormalizacionSecuenciaPort {

    private final JdbcTemplate jdbcTemplate;
    private final AuditManualChange auditManual;

    @Override
    public int reservarNumero(Integer ejercicioId, Integer anio) {
        jdbcTemplate.update(
                "INSERT INTO compra_formalizacion_secuencia (ejercicio_id, anio, ultimo_numero, created, updated) "
                        + "VALUES (?, ?, LAST_INSERT_ID(1), NOW(6), NOW(6)) "
                        + "ON DUPLICATE KEY UPDATE ultimo_numero = LAST_INSERT_ID(ultimo_numero + 1)",
                ejercicioId, anio);
        Integer ultimo = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Integer.class);
        int reservado = ultimo == null ? 0 : ultimo;
        auditManual.sequence("CompraFormalizacionSecuencia", "compra_formalizacion_secuencia", ejercicioId, anio, reservado);
        return reservado;
    }
}
