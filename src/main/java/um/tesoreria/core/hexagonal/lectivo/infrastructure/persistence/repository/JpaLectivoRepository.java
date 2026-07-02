/**
 *
 */
package um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.persistence.entity.LectivoEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaLectivoRepository extends JpaRepository<LectivoEntity, Integer> {

    List<LectivoEntity> findAllByLectivoIdIn(List<Integer> ids, Sort sort);

    Optional<LectivoEntity> findByLectivoId(Integer lectivoId);

    Optional<LectivoEntity> findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(OffsetDateTime now, OffsetDateTime now2);

    Optional<LectivoEntity> findTopByOrderByLectivoIdDesc();

}
