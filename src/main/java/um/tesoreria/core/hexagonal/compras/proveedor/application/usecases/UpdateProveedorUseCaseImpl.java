package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.UpdateProveedorUseCase;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateProveedorUseCaseImpl implements UpdateProveedorUseCase {
    private final ProveedorRepository repository;

    @Override
    public Optional<Proveedor> updateProveedor(Integer id, Proveedor proveedor) {
        if (proveedor.getEmailInterno() == null) {
            proveedor.setEmailInterno("");
        }
        if (proveedor.getHabilitado() == null) {
            proveedor.setHabilitado((byte) 0);
        }
        return repository.update(id, proveedor);
    }
}
