package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorSearch;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.SearchProveedoresUseCase;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchProveedoresUseCaseImpl implements SearchProveedoresUseCase {
    private final ProveedorRepository repository;

    @Override
    public List<ProveedorSearch> searchProveedores(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }
}
