package um.tesoreria.core.hexagonal.compras.proveedor.domain.model;

/** Id, razón social y CUIT de un proveedor, sin el resto de sus datos ni su cuenta. */
public record ProveedorNombre(Integer proveedorId, String razonSocial, String cuit) {
}
