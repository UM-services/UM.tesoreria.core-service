package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion.TrazaLinea;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out.TrazaContableOrdenPagoRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Lee la traza consolidada de la vista del legacy {@code vw_trazaContablePagosConsolidada}
 * (importe neto por cuenta de los asientos de una orden de pago) y le agrega el
 * nombre de la cuenta desde {@code plancta}. La consulta es nativa a propósito: la
 * vista no es una entidad del core y no hay otro consumidor.
 */
@Component
@RequiredArgsConstructor
public class JdbcTrazaContableOrdenPagoRepositoryAdapter implements TrazaContableOrdenPagoRepository {

    private static final String SQL = """
            SELECT t.MCo_Pla_Cuenta AS numero_cuenta,
                   (SELECT MAX(p.pla_nombre) FROM plancta p WHERE p.pla_cuenta = t.MCo_Pla_Cuenta) AS nombre_cuenta,
                   t.MCo_Debita AS debita,
                   t.MCo_Importe AS importe
              FROM vw_trazaContablePagosConsolidada t
             WHERE t.MvP_ID = :id
             ORDER BY t.MCo_Debita DESC, t.MCo_Pla_Cuenta
            """;

    private final JdbcClient jdbcClient;

    @Override
    public List<TrazaLinea> findConsolidadaByOrdenPagoId(Long proveedorMovimientoId) {
        return jdbcClient.sql(SQL)
                .param("id", proveedorMovimientoId)
                .query((rs, rowNum) -> {
                    BigDecimal importe = rs.getBigDecimal("importe");
                    boolean debita = rs.getInt("debita") == 1;
                    return new TrazaLinea(
                            rs.getBigDecimal("numero_cuenta"),
                            rs.getString("nombre_cuenta"),
                            debita ? importe : BigDecimal.ZERO,
                            debita ? BigDecimal.ZERO : importe);
                })
                .list();
    }
}