package um.tesoreria.core.hexagonal.compras.proveedor.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorNombre;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in.GetNombresByProveedorIdsUseCase;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.out.ProveedorRepository;

import java.util.Collection;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetNombresByProveedorIdsUseCaseImpl implements GetNombresByProveedorIdsUseCase {

    private final ProveedorRepository repository;

    @Override
    public List<ProveedorNombre> getNombresByProveedorIds(Collection<Integer> proveedorIds) {
        if (proveedorIds == null || proveedorIds.isEmpty()) {
            return List.of();
        }
        return repository.findNombresByProveedorIdIn(proveedorIds);
    }
}
