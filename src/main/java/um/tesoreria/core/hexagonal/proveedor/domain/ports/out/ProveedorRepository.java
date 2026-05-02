package um.tesoreria.core.hexagonal.proveedor.domain.ports.out;

import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;

import java.util.List;
import java.util.Optional;

public interface ProveedorRepository {
    Proveedor create(Proveedor proveedor);
    Optional<Proveedor> findByProveedorId(Integer idproveedorId);
    Optional<Proveedor> findByCuit(String cuit);
    Optional<Proveedor> findLast();
    List<Proveedor> findAll();
    List<ProveedorSearch> findAllByStrings(List<String> conditions);
    Optional<Proveedor> update(Integer proveedorId, Proveedor proveedor);
    boolean deleteById(Integer proveedorId);
}
