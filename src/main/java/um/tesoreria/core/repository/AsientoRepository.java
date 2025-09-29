/**
 * 
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.Asiento;


/**
 * @author daniel
 *
 */
@Repository
public interface AsientoRepository extends JpaRepository<Asiento, Long> {

	Optional<Asiento> findByFechaAndOrden(OffsetDateTime fecha, Integer orden);

	Optional<Asiento> findByAsientoId(Long asientoId);

	@Modifying
	void deleteByAsientoId(Long asientoId);

}
