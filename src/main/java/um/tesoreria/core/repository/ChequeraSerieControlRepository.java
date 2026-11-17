/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ChequeraSerieControl;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraSerieControlRepository extends JpaRepository<ChequeraSerieControl, Long> {

	public Optional<ChequeraSerieControl> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	public Optional<ChequeraSerieControl> findByChequeraSerieControlId(Long chequeraSerieControlId);

	public Optional<ChequeraSerieControl> findTopByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc(Integer facultadId,
			Integer tipoChequeraId);

	/**
	 * Igual que {@link #findTopByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc} pero
	 * con bloqueo pesimista para reservar números de chequera sin colisiones.
	 */
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	public Optional<ChequeraSerieControl> findFirstByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc(Integer facultadId,
			Integer tipoChequeraId);
}
