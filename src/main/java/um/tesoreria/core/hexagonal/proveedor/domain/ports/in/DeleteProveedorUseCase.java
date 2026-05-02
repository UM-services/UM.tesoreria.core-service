package um.tesoreria.core.hexagonal.proveedor.domain.ports.in;

public interface DeleteProveedorUseCase {
    boolean deleteProveedor(Integer proveedorId);
}
