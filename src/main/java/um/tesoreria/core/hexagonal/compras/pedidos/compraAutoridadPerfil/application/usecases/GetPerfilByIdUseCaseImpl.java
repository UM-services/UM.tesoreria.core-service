package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.GetPerfilByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetPerfilByIdUseCaseImpl implements GetPerfilByIdUseCase {

    private final CompraAutoridadPerfilRepository repository;

    @Override
    public Optional<CompraAutoridadPerfil> getById(Long autoridadPerfilId) {
        return repository.findByAutoridadPerfilId(autoridadPerfilId);
    }

}
