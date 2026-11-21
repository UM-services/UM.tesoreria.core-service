package um.tesoreria.core.hexagonal.contable.asiento.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Resultado de consultar/generar/visualizar un asiento contable. */
public record AsientoContableResultado(
        OffsetDateTime fecha,
        Integer orden,
        String vinculo,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        boolean cuadra,
        List<AsientoContableLinea> lineas) {
}
