package um.tesoreria.core.hexagonal.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.DeleteProveedorUseCase;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;

@Component
@RequiredArgsConstructor
public class DeleteProveedorUseCaseImpl implements DeleteProveedorUseCase {
    private final ProveedorRepository repository;

    @Override
    public boolean deleteProveedor(Integer id) {
        return repository.deleteById(id);
    }
}
