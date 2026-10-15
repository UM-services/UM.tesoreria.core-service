package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;

import java.util.Optional;

@Repository
public interface JpaRolRepository extends JpaRepository<RolEntity, Long> {

    Optional<RolEntity> findByRolId(Long rolId);

    boolean existsByRolId(Long rolId);

    void deleteByRolId(Long rolId);
}
