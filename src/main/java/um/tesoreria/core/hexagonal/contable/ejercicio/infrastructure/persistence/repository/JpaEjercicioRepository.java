package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.entity.EjercicioEntity;

import java.util.Optional;

@Repository
public interface JpaEjercicioRepository extends JpaRepository<EjercicioEntity, Integer> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EjercicioEntity> findTopByOrderByEjercicioIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EjercicioEntity> findWithLockByEjercicioId(Integer ejercicioId);
}
