package um.tesoreria.core.hexagonal.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.model.PaginatedResponse;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.GetPaginatedProveedoresUseCase;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;

@Component
@RequiredArgsConstructor
public class GetPaginatedProveedoresUseCaseImpl implements GetPaginatedProveedoresUseCase {
    private final ProveedorRepository repository;

    @Override
    public PaginatedResponse<Proveedor> getPaginatedProveedores(int page, int size) {
        return repository.findAllPaginated(page, size);
    }
}
