package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.persistence.entity.CompraAutoridadUsuarioEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCompraAutoridadUsuarioRepository
        extends JpaRepository<CompraAutoridadUsuarioEntity, Long> {

    List<CompraAutoridadUsuarioEntity> findAllByUsuarioIdOrderByAutoridadPerfilIdAsc(Integer usuarioId);

    Optional<CompraAutoridadUsuarioEntity> findByUsuarioIdAndAutoridadPerfilId(Integer usuarioId, Long autoridadPerfilId);

    boolean existsByUsuarioIdAndAutoridadPerfilId(Integer usuarioId, Long autoridadPerfilId);

    void deleteByUsuarioIdAndAutoridadPerfilId(Integer usuarioId, Long autoridadPerfilId);

}
