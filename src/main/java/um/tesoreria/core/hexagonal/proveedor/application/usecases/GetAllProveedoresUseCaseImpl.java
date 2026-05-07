package um.tesoreria.core.hexagonal.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.in.GetAllProveedoresUseCase;
import um.tesoreria.core.hexagonal.proveedor.domain.ports.out.ProveedorRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllProveedoresUseCaseImpl implements GetAllProveedoresUseCase {

    private final ProveedorRepository repository;

    @Override
    public List<Proveedor> getAllProveedores() {
        return repository.findAll();
    }

}
