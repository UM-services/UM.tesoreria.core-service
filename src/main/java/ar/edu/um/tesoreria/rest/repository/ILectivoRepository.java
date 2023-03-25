/**
 *
 */
package ar.edu.um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Lectivo;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ILectivoRepository extends JpaRepository<Lectivo, Integer> {

    List<Lectivo> findAllByLectivoIdIn(List<Integer> ids, Sort sort);

    Optional<Lectivo> findByLectivoId(Integer lectivoId);

    Optional<Lectivo> findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(OffsetDateTime now, OffsetDateTime now2);

    Optional<Lectivo> findTopByOrderByLectivoIdDesc();

}
