package um.tesoreria.core.hexagonal.proveedor.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.*;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProveedorService {

    private final CreateProveedorUseCase createProveedorUseCase;
    private final GetProveedorByIdUseCase getProveedorByIdUseCase;
    private final GetProveedorByCuitUseCase getProveedorByCuitUseCase;
    private final GetLastProveedorUseCase getLastProveedorUseCase;
    private final GetAllProveedoresUseCase getAllProveedoresUseCase;
    private final SearchProveedoresUseCase searchProveedoresUseCase;
    private final UpdateProveedorUseCase updateProveedorUseCase;
    private final DeleteProveedorUseCase deleteProveedorUseCase;

    public Proveedor create(Proveedor proveedor) {
        return createProveedorUseCase.createProveedor(proveedor);
    }

    public Optional<Proveedor> getByProveedorId(Integer proveedorId) {
        return getProveedorByIdUseCase.getProveedorById(proveedorId);
    }

    public Optional<Proveedor> getByCuit(String cuit) {
        return getProveedorByCuitUseCase.getProveedorByCuit(cuit);
    }

    public Optional<Proveedor> getLast() {
        return getLastProveedorUseCase.getLastProveedor();
    }

    public List<Proveedor> getAll() {
        return getAllProveedoresUseCase.getAllProveedores();
    }

    public List<ProveedorSearch> search(List<String> conditions) {
        return searchProveedoresUseCase.searchProveedores(conditions);
    }

    public Optional<Proveedor> update(Integer proveedorId, Proveedor proveedor) {
        return updateProveedorUseCase.updateProveedor(proveedorId, proveedor);
    }

    public boolean delete(Integer proveedorId) {
        return deleteProveedorUseCase.deleteProveedor(proveedorId);
    }
}
