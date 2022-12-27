/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Lectivo;

/**
 * @author daniel
 *
 */
@Repository
public interface ILectivoRepository extends JpaRepository<Lectivo, Integer>{

	public List<Lectivo> findAllByLectivoIdIn(List<Integer> ids, Sort sort);

	public Optional<Lectivo> findByLectivoId(Integer lectivoId);

	public Optional<Lectivo> findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(OffsetDateTime now, OffsetDateTime now2);

	public Optional<Lectivo> findTopByOrderByLectivoIdDesc();

}
