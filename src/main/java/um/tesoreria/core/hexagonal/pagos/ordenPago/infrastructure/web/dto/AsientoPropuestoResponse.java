package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Asiento propuesto con su fecha/orden (orden nulo = se reserva al registrar). */
public record AsientoPropuestoResponse(OffsetDateTime fechaContable,
                                       Integer ordenContable,
                                       List<AsientoOrigenResponse> origenes,
                                       List<AsientoLineaResponse> lineas,
                                       BigDecimal totalDebe,
                                       BigDecimal totalHaber,
                                       boolean cuadra) {
}
