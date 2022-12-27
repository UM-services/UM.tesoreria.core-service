/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.ChequeraTotal;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraTotalRepository extends JpaRepository<ChequeraTotal, Long> {

	public List<ChequeraTotal> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	@Modifying
	public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
