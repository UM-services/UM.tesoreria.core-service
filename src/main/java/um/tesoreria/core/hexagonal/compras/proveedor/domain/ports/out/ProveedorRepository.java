package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorSearch;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository {
    Proveedor create(Proveedor proveedor);
    Optional<Proveedor> findByProveedorId(Integer idproveedorId);
    Optional<Proveedor> findByCuit(String cuit);
    /**
     * @param excluirProveedorId proveedor a ignorar (el que se está editando); {@code null} en un alta
     * @return {@code true} si otro proveedor ya tiene exactamente ese CUIT
     */
    boolean existsOtroConCuit(String cuit, Integer excluirProveedorId);
    Optional<Proveedor> findLast();
    List<Proveedor> findAll();
    um.tesoreria.core.model.PaginatedResponse<Proveedor> findAllPaginated(int page, int size);
    List<ProveedorSearch> findAllByStrings(List<String> conditions);
    Optional<Proveedor> update(Integer proveedorId, Proveedor proveedor);
    boolean deleteById(Integer proveedorId);
}
