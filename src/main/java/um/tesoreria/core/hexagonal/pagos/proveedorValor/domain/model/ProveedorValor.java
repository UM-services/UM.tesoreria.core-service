package um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model;

public record ProveedorValor(
        Long proveedorValorId,
        Long proveedorMovimientoId,
        Integer orden,
        Long valorMovimientoId
) {
}
