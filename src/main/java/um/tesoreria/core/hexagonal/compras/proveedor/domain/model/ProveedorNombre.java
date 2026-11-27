package um.tesoreria.core.hexagonal.compras.proveedor.domain.model;

/**
 * Id, razón social y CUIT de un proveedor, sin el resto de sus datos ni su cuenta. {@code cuit} viene {@code null}
 * cuando el proveedor no tiene dato ({@link ProveedorCuit#sinDato}); así hay un único valor para "sin CUIT".
 */
public record ProveedorNombre(Integer proveedorId, String razonSocial, String cuit) {
}
