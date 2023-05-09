/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.Asiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;


/**
 * @author daniel
 *
 */
@Repository
public interface IAsientoRepository extends JpaRepository<Asiento, Long> {

	public Optional<Asiento> findByFechaAndOrden(OffsetDateTime fecha, Integer orden);

	public Optional<Asiento> findByAsientoId(Long asientoId);

	@Modifying
	public void deleteByAsientoId(Long asientoId);

}
