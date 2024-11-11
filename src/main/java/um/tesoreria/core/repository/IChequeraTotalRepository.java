/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import com.netflix.spectator.api.histogram.PercentileBuckets;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.ChequeraTotal;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraTotalRepository extends JpaRepository<ChequeraTotal, Long> {

	List<ChequeraTotal> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	Optional<ChequeraTotal> findByChequeraTotalId(Long chequeraTotalId);

	Optional<ChequeraTotal> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
