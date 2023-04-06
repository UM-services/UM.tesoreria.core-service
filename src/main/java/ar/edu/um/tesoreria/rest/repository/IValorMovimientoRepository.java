/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.ValorMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IValorMovimientoRepository extends JpaRepository<ValorMovimiento, Long> {

	public List<ValorMovimiento> findAllByValorMovimientoIdIn(List<Long> valorMovimientoIds);

	public Optional<ValorMovimiento> findByValorIdAndNumero(Integer valorId, Long numero);

	public Optional<ValorMovimiento> findByValorMovimientoId(Long valorMovimientoId);

	public Optional<ValorMovimiento> findFirstByFechaContableAndOrdenContable(OffsetDateTime fechaContable,
			Integer ordenContable);

	@Modifying
	public void deleteByValorMovimientoId(Long valorMovimientoId);
}
