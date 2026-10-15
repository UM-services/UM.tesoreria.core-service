package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.CreatePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;

@Component
@RequiredArgsConstructor
public class CreatePerfilUseCaseImpl implements CreatePerfilUseCase {

    private final CompraAutoridadPerfilRepository repository;

    @Override
    public CompraAutoridadPerfil create(CompraAutoridadPerfil perfil) {
        if (perfil == null || perfil.getNombre() == null || perfil.getNombre().isBlank()) {
            throw new CompraAutoridadPerfilException("El nombre del perfil de autoridad es requerido");
        }
        if (perfil.getMultiplico() != null && perfil.getMultiplico() < 1) {
            throw new CompraAutoridadPerfilException(
                    "El múltiplo debe ser mayor o igual a 1 (usar nulo para autorizar sin límite)");
        }
        if (repository.findByNombre(perfil.getNombre()).isPresent()) {
            throw new CompraAutoridadPerfilException("Ya existe el perfil de autoridad " + perfil.getNombre());
        }
        return repository.save(perfil);
    }

}
