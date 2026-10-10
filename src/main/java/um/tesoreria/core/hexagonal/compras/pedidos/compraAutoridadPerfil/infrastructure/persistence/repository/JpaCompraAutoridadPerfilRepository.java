package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadPerfil.infrastructure.persistence.entity.CompraAutoridadPerfilEntity;

import java.util.Optional;

@Repository
public interface JpaCompraAutoridadPerfilRepository extends JpaRepository<CompraAutoridadPerfilEntity, Long> {

    Optional<CompraAutoridadPerfilEntity> findByAutoridadPerfilId(Long autoridadPerfilId);

    Optional<CompraAutoridadPerfilEntity> findByNombre(String nombre);

    boolean existsByAutoridadPerfilId(Long autoridadPerfilId);

    void deleteByAutoridadPerfilId(Long autoridadPerfilId);

}
