package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import java.math.BigDecimal;
import java.util.List;

public record ProductoEstadoResponse(
        Integer productoId,
        String nombre,
        String tituloCuota,
        Integer totalCuotas,
        BigDecimal total,
        BigDecimal pagado,
        List<CuotaEstadoResponse> cuotas) {
}