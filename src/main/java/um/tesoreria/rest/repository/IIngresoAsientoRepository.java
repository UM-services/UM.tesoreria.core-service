/**
 * 
 */
package um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.rest.model.IngresoAsiento;

/**
 * @author daniel
 *
 */
@Repository
public interface IIngresoAsientoRepository extends JpaRepository<IngresoAsiento, Long> {

	public Optional<IngresoAsiento> findByFechaContableAndTipoPagoId(OffsetDateTime fechaContable, Integer tipoPagoId);

}
