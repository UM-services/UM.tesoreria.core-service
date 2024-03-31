/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ChequeraSerieControl;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraSerieControlRepository extends JpaRepository<ChequeraSerieControl, Long> {

	public Optional<ChequeraSerieControl> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	public Optional<ChequeraSerieControl> findByChequeraSerieControlId(Long chequeraSerieControlId);

	public Optional<ChequeraSerieControl> findTopByFacultadIdAndTipoChequeraIdOrderByChequeraSerieIdDesc(Integer facultadId,
			Integer tipoChequeraId);
}
