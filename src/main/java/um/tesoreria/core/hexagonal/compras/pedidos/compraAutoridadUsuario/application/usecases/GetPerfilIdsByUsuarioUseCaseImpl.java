package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.exception.CompraAutoridadUsuarioException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.in.GetPerfilIdsByUsuarioUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetPerfilIdsByUsuarioUseCaseImpl implements GetPerfilIdsByUsuarioUseCase {

    private final CompraAutoridadUsuarioRepository repository;

    @Override
    public List<Long> getPerfilIds(Integer usuarioId) {
        if (usuarioId == null) {
            throw new CompraAutoridadUsuarioException("usuarioId es requerido");
        }
        return repository.findPerfilIdsByUsuarioId(usuarioId);
    }

}
