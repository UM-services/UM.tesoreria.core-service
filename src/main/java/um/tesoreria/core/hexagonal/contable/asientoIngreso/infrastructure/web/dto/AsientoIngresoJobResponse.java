package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record AsientoIngresoJobResponse(
        Long jobId,
        String estado,
        OffsetDateTime fechaDesde,
        OffsetDateTime fechaHasta,
        int totalTareas,
        int tareasOk,
        int tareasError,
        int tareasSinMovimientos,
        int tareasPendientes,
        double porcentaje,
        OffsetDateTime fechaActual,
        Integer tipoPagoActual,
        Long usuarioId,
        OffsetDateTime iniciado,
        OffsetDateTime finalizado,
        String mensaje) {
}
