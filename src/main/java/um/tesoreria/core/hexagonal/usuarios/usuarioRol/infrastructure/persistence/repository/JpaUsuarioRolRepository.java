package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.entity.UsuarioRolEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUsuarioRolRepository extends JpaRepository<UsuarioRolEntity, Long> {

    List<UsuarioRolEntity> findAllByUserId(Long userId);

    Optional<UsuarioRolEntity> findByUserIdAndRolId(Long userId, Long rolId);

    void deleteByUserIdAndRolId(Long userId, Long rolId);
}
