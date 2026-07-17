package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

public interface DeleteProveedorUseCase {
    boolean deleteProveedor(Integer proveedorId);
}
