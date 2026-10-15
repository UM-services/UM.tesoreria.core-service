package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.AsignarAutoridadUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;

@Component
@RequiredArgsConstructor
public class AsignarAutoridadUsuarioUseCaseImpl implements AsignarAutoridadUsuarioUseCase {

    private final CompraAutoridadUsuarioRepository repository;

    @Override
    public void asignar(Integer usuarioId, Long autoridadPerfilId) {
        repository.asignar(usuarioId, autoridadPerfilId);
    }

}
