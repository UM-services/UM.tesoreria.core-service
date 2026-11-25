package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.persistence.repository;

/** Proyección cerrada: id y razón social, sin cargar la cuenta del proveedor. */
public interface ProveedorNombreProjection {

    Integer getProveedorId();

    String getRazonSocial();
}
