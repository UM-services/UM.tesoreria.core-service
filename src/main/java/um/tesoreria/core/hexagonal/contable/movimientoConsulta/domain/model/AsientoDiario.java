package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Un asiento (fecha y orden) con sus líneas y la suma de cada lado. */
public record AsientoDiario(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        List<LineaDiario> lineas,
        BigDecimal totalDebe,
        BigDecimal totalHaber) {
}
