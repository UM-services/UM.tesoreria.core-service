package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaEstadoResponse(
        Integer cuotaId,
        Integer mes,
        Integer anho,
        LocalDate primerVencimiento,
        BigDecimal importe,
        Integer ordenPago,
        LocalDate fechaPago,
        BigDecimal importePagado,
        String referenciaPago) {
}