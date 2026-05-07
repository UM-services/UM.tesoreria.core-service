package um.tesoreria.core.hexagonal.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.GetProveedorByCuitUseCase;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetProveedorByCuitUseCaseImpl implements GetProveedorByCuitUseCase {
    private final ProveedorRepository repository;

    @Override
    public Optional<Proveedor> getProveedorByCuit(String cuit) {
        return repository.findByCuit(cuit);
    }
}
