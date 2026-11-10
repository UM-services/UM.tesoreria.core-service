package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.model.CompraAutoridadUsuario;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.GetAsignacionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAsignacionesUseCaseImpl implements GetAsignacionesUseCase {

    private final CompraAutoridadUsuarioRepository repository;

    @Override
    public List<CompraAutoridadUsuario> getAsignaciones() {
        return repository.findAll();
    }

}
