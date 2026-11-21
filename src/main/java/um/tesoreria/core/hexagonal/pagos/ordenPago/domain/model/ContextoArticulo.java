package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

public record ContextoArticulo(BigDecimal numeroCuenta, BigDecimal precioFinal) {
}
