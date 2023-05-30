/**
 * 
 */
package um.tesoreria.rest.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.ChequeraEliminada;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraEliminadaRepository extends JpaRepository<ChequeraEliminada, Long> {

	public Optional<ChequeraEliminada> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

}
