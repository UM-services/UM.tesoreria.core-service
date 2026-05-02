package um.tesoreria.core.hexagonal.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.GetLastProveedorUseCase;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLastProveedorUseCaseImpl implements GetLastProveedorUseCase {
    private final ProveedorRepository repository;

    @Override
    public Optional<Proveedor> getLastProveedor() {
        return repository.findLast();
    }
}
