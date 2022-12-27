/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.time.OffsetDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.Asiento;

/**
 * @author daniel
 *
 */
@Repository
public interface IAsientoRepository extends JpaRepository<Asiento, Long> {

	public Asiento findByFechaAndOrden(OffsetDateTime fecha, Integer orden);

	@Modifying
	public void deleteByAsientoId(Long asientoId);

}
