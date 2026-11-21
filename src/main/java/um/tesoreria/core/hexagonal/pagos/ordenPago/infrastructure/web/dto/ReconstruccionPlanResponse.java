package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.util.List;

/**
 * Resultado del cálculo de reconstrucción de traza (dry-run o aplicado).
 */
public record ReconstruccionPlanResponse(Long proveedorMovimientoId, Integer prefijo, Long numeroComprobante,
                                         List<ReconstruccionAsientoResponse> asientos, int cantidadCrear,
                                         int cantidadActualizar, int cantidadSinCambios, boolean requiereForzar,
                                         boolean sinCambios, List<String> errores) {
}
