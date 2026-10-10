package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.QuitarAutoridadUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;

@Component
@RequiredArgsConstructor
public class QuitarAutoridadUsuarioUseCaseImpl implements QuitarAutoridadUsuarioUseCase {

    private final CompraAutoridadUsuarioRepository repository;

    @Override
    public void quitar(Integer usuarioId, Long autoridadPerfilId) {
        repository.quitar(usuarioId, autoridadPerfilId);
    }

}
