package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.entity.ArticuloEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface JpaArticuloRepository extends JpaRepository<ArticuloEntity, Long> {
    Optional<ArticuloEntity> findTopByOrderByArticuloIdDesc();
    Page<ArticuloEntity> findAllByTipo(String tipo, Pageable pageable);

    /** Lectura con bloqueo de la fila (SELECT ... FOR UPDATE). */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ArticuloEntity> findWithLockByArticuloId(Long articuloId);

}
