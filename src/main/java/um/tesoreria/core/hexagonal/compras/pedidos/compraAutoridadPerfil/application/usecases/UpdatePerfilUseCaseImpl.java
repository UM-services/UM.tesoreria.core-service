package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.application.exception.CompraAutoridadPerfilException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.model.CompraAutoridadPerfil;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.in.UpdatePerfilUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.domain.ports.out.CompraAutoridadPerfilRepository;

@Component
@RequiredArgsConstructor
public class UpdatePerfilUseCaseImpl implements UpdatePerfilUseCase {

    private final CompraAutoridadPerfilRepository repository;

    @Override
    public CompraAutoridadPerfil update(CompraAutoridadPerfil newPerfil, Long autoridadPerfilId) {
        if (newPerfil == null) {
            throw new CompraAutoridadPerfilException("El perfil de autoridad es requerido");
        }
        if (newPerfil.getMultiplico() != null && newPerfil.getMultiplico() < 1) {
            throw new CompraAutoridadPerfilException(
                    "El múltiplo debe ser mayor o igual a 1 (usar nulo para autorizar sin límite)");
        }
        return repository.findByAutoridadPerfilId(autoridadPerfilId).map(perfil -> {
            String nombre = newPerfil.getNombre() != null ? newPerfil.getNombre() : perfil.getNombre();
            repository.findByNombre(nombre)
                    .filter(otro -> !otro.getAutoridadPerfilId().equals(autoridadPerfilId))
                    .ifPresent(otro -> {
                        throw new CompraAutoridadPerfilException("Ya existe el perfil de autoridad " + nombre);
                    });
            perfil.setNombre(nombre);
            perfil.setMultiplico(newPerfil.getMultiplico());
            if (newPerfil.getActivo() != null) {
                perfil.setActivo(newPerfil.getActivo());
            }
            return repository.save(perfil);
        }).orElse(null);
    }

}
