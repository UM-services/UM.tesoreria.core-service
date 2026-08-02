package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.persistence.entity.GuaraniUbicacionEntity;

@Repository
public interface JpaGuaraniUbicacionRepository extends JpaRepository<GuaraniUbicacionEntity, Integer> {
}
