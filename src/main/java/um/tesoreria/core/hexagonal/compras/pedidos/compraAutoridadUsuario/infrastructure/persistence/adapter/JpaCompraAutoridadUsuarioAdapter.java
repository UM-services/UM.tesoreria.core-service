package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.adapter;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.domain.ports.out.CompraAutoridadUsuarioRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.entity.CompraAutoridadUsuarioEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.repository.JpaCompraAutoridadUsuarioRepository;

import java.util.List;

/**
 * Persistencia JPA de la tabla puente usuario ↔ perfil de autoridad.
 *
 * <p>Se migró desde SQL directo para que las asignaciones queden en la auditoría: el alta y la
 * baja pasan por {@code save}/{@code delete} y disparan los listeners de Hibernate.</p>
 */
@Component
@RequiredArgsConstructor
public class JpaCompraAutoridadUsuarioAdapter implements CompraAutoridadUsuarioRepository {

    private final JpaCompraAutoridadUsuarioRepository repository;

    @Override
    public List<Long> findPerfilIdsByUsuarioId(Integer usuarioId) {
        return repository.findAllByUsuarioIdOrderByAutoridadPerfilIdAsc(usuarioId).stream()
                .map(CompraAutoridadUsuarioEntity::getAutoridadPerfilId)
                .toList();
    }

    @Override
    @Transactional
    public void asignar(Integer usuarioId, Long autoridadPerfilId) {
        if (repository.existsByUsuarioIdAndAutoridadPerfilId(usuarioId, autoridadPerfilId)) {
            return;
        }
        repository.save(new CompraAutoridadUsuarioEntity(usuarioId, autoridadPerfilId));
    }

    @Override
    @Transactional
    public void quitar(Integer usuarioId, Long autoridadPerfilId) {
        repository.deleteByUsuarioIdAndAutoridadPerfilId(usuarioId, autoridadPerfilId);
    }

    @Override
    public boolean existe(Integer usuarioId, Long autoridadPerfilId) {
        return repository.existsByUsuarioIdAndAutoridadPerfilId(usuarioId, autoridadPerfilId);
    }

}
