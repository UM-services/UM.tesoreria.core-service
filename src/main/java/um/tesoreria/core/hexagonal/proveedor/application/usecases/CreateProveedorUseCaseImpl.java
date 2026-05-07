package um.tesoreria.core.hexagonal.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.CreateProveedorUseCase;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;

@Component
@RequiredArgsConstructor
public class CreateProveedorUseCaseImpl implements CreateProveedorUseCase {
    private final ProveedorRepository repository;

    @Override
    public Proveedor createProveedor(Proveedor proveedor) {
        return repository.create(proveedor);
    }
}
