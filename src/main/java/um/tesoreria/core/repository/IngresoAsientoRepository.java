/**
 * 
 */
package um.tesoreria.core.repository;

import java.time.OffsetDateTime;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.IngresoAsiento;

/**
 * @author daniel
 *
 */
@Repository
public interface IngresoAsientoRepository extends JpaRepository<IngresoAsiento, Long> {

	public Optional<IngresoAsiento> findByFechaContableAndTipoPagoId(OffsetDateTime fechaContable, Integer tipoPagoId);

	/**
	 * Bloquea la fila del mapa (fecha, tipo de pago) para serializar dos regeneraciones del mismo
	 * asiento. {@code findFirst...} evita chocar con el derivado existente y sigue siendo una
	 * consulta derivada (sin {@code @Query}).
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public Optional<IngresoAsiento> findFirstByFechaContableAndTipoPagoId(OffsetDateTime fechaContable, Integer tipoPagoId);

}
