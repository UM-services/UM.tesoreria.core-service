package um.tesoreria.core.service.facade.mayor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Pedido de consulta del mayor. {@code desde} y {@code hasta} pueden abarcar uno o más ejercicios consecutivos.
 * Las cuentas se eligen de <b>una</b> de dos formas (nunca ambas): {@code cuentas} (una o varias, cualquiera) o
 * el rango {@code cuentaDesde}–{@code cuentaHasta}.
 */
public record MayorConsultaRequest(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<BigDecimal> cuentas,
        BigDecimal cuentaDesde,
        BigDecimal cuentaHasta) {
}