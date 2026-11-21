package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model;

import java.math.BigDecimal;

/**
 * Tipo de valor (tabla {@code valor}). Define la cuenta contable por defecto y la categoría
 * usada en el libro de banco.
 */
public record Valor(
        Integer valorId,
        String concepto,
        String categoria,
        Integer retardo,
        Integer vencimiento,
        Integer proveedorId,
        BigDecimal numeroCuenta) {
}
