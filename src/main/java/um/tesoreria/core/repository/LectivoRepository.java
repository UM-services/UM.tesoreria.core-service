/**
 *
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Lectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface LectivoRepository extends JpaRepository<Lectivo, Integer> {

    List<Lectivo> findAllByLectivoIdIn(List<Integer> ids, Sort sort);

    Optional<Lectivo> findByLectivoId(Integer lectivoId);

    Optional<Lectivo> findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(OffsetDateTime now, OffsetDateTime now2);

    Optional<Lectivo> findTopByOrderByLectivoIdDesc();

}
