/**
 * 
 */
package um.tesoreria.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import um.tesoreria.rest.model.ChequeraPagoAsiento;

/**
 * @author daniel
 *
 */
public interface IChequeraPagoAsientoRepository extends JpaRepository<ChequeraPagoAsiento, Long> {

	@Modifying
	public void deleteAllByFacultadIdAndTipochequeraIdAndChequeraserieId(Integer facultadId, Integer tipochequeraId,
			Long chequeraserieId);

}
