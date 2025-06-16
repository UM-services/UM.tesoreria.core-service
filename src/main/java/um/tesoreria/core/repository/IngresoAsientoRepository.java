/**
 * 
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.IngresoAsiento;

/**
 * @author daniel
 *
 */
@Repository
public interface IngresoAsientoRepository extends JpaRepository<IngresoAsiento, Long> {

	public Optional<IngresoAsiento> findByFechaContableAndTipoPagoId(OffsetDateTime fechaContable, Integer tipoPagoId);

}
