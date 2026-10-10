package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.GetAllPerfilesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllPerfilesUseCaseImpl implements GetAllPerfilesUseCase {

    private final CompraAutoridadPerfilRepository repository;

    @Override
    public List<CompraAutoridadPerfil> getAll() {
        return repository.findAll();
    }

}
