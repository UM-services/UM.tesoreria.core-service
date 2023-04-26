/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraAlternativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraAlternativaRepository extends JpaRepository<ChequeraAlternativa, Long> {

	public List<ChequeraAlternativa> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

	public Optional<ChequeraAlternativa> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
			Integer alternativaId);

	@Modifying
	public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
