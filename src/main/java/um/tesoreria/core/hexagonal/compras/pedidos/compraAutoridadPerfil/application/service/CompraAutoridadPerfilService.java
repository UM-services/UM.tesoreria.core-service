package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.CreatePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.DeletePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.GetAllPerfilesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.GetPerfilByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.UpdatePerfilUseCase;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CompraAutoridadPerfilService {

    private final GetAllPerfilesUseCase getAllPerfilesUseCase;
    private final GetPerfilByIdUseCase getPerfilByIdUseCase;
    private final CreatePerfilUseCase createPerfilUseCase;
    private final UpdatePerfilUseCase updatePerfilUseCase;
    private final DeletePerfilUseCase deletePerfilUseCase;

    public List<CompraAutoridadPerfil> findAll() {
        return getAllPerfilesUseCase.getAll();
    }

    public Optional<CompraAutoridadPerfil> findById(Long autoridadPerfilId) {
        return getPerfilByIdUseCase.getById(autoridadPerfilId);
    }

    public CompraAutoridadPerfil add(CompraAutoridadPerfil perfil) {
        return createPerfilUseCase.create(perfil);
    }

    public CompraAutoridadPerfil update(CompraAutoridadPerfil perfil, Long autoridadPerfilId) {
        CompraAutoridadPerfil updated = updatePerfilUseCase.update(perfil, autoridadPerfilId);
        if (updated == null) {
            throw new CompraAutoridadPerfilException(autoridadPerfilId);
        }
        return updated;
    }

    public void delete(Long autoridadPerfilId) {
        deletePerfilUseCase.delete(autoridadPerfilId);
    }

}
