package um.tesoreria.core.hexagonal.compras.proveedor.domain.model;

/** Id y razón social de un proveedor, sin el resto de sus datos ni su cuenta. */
public record ProveedorNombre(Integer proveedorId, String razonSocial) {
}
