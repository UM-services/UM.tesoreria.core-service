package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository;

/** Proyección cerrada: id, razón social y CUIT, sin cargar la cuenta del proveedor. */
public interface ProveedorNombreProjection {

    Integer getProveedorId();

    String getRazonSocial();

    String getCuit();
}
