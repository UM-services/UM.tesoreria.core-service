package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Una cuota con su primer pago; {@code ordenPago}, {@code fechaPago}, {@code importePagado} y
 * {@code referenciaPago} son {@code null} mientras esté impaga. {@code estado} lo resuelve este servicio
 * (ver {@link EstadoCuota}) para que cada consumidor muestre lo mismo.
 */
public record CuotaEstado(
        Integer cuotaId,
        Integer mes,
        Integer anho,
        LocalDate primerVencimiento,
        BigDecimal importe,
        Integer ordenPago,
        LocalDate fechaPago,
        BigDecimal importePagado,
        String referenciaPago,
        EstadoCuota estado) {
}