package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AsientoIngresoResultadoResponse(
        OffsetDateTime fecha,
        Integer tipoPagoId,
        String tipoPagoNombre,
        boolean existe,
        Integer ordenContable,
        String vinculo,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        boolean cuadra,
        List<AsientoIngresoLineaResponse> lineas,
        String accion,
        List<CuentaFaltanteResponse> faltantes) {
}
