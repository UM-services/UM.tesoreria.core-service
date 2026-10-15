package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUbicacionArticuloRepository extends JpaRepository<UbicacionArticuloEntity, Long> {
    Optional<UbicacionArticuloEntity> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId);
    List<UbicacionArticuloEntity> findAllByArticuloId(Long articuloId);

    /** Solo el id: no carga la entidad, así la lectura con bloqueo posterior es la que trae su estado. */
    Optional<SoloId> findIdByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId);

    /** Lectura con bloqueo de la fila (SELECT ... FOR UPDATE). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<UbicacionArticuloEntity> findWithLockByUbicacionArticuloId(Long ubicacionArticuloId);

    /** Lectura con bloqueo de los vínculos del artículo, solo escalares (SELECT ... FOR UPDATE). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    List<Escalares> findWithLockByArticuloIdOrderByUbicacionArticuloId(Long articuloId);

    interface SoloId {
        Long getUbicacionArticuloId();
    }

    interface Escalares {
        Long getUbicacionArticuloId();
        Integer getUbicacionId();
        Long getArticuloId();
        BigDecimal getNumeroCuenta();
    }
}
