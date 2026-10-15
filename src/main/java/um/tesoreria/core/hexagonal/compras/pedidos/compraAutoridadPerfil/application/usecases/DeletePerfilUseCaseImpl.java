package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.DeletePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;

@Component
@RequiredArgsConstructor
public class DeletePerfilUseCaseImpl implements DeletePerfilUseCase {

    private final CompraAutoridadPerfilRepository repository;

    @Override
    public void delete(Long autoridadPerfilId) {
        if (autoridadPerfilId == null) {
            throw new CompraAutoridadPerfilException("autoridadPerfilId es requerido");
        }
        if (!repository.existsByAutoridadPerfilId(autoridadPerfilId)) {
            throw new CompraAutoridadPerfilException(autoridadPerfilId);
        }
        repository.deleteByAutoridadPerfilId(autoridadPerfilId);
    }

}
