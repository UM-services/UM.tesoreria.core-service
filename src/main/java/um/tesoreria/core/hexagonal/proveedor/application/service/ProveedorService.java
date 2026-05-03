package um.tesoreria.core.hexagonal.proveedor.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.*;

import java.util.List;
import java.util.Optional;
import um.tesoreria.core.model.PaginatedResponse;

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
    private final GetPaginatedProveedoresUseCase getPaginatedProveedoresUseCase;


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

    public PaginatedResponse<Proveedor> getPaginated(int page, int size) {
        return getPaginatedProveedoresUseCase.getPaginatedProveedores(page, size);
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
