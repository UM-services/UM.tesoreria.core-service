/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 *
 */
public interface ChequeraPagoAsientoRepository extends JpaRepository<ChequeraPagoAsiento, Long> {

	List<ChequeraPagoAsiento> findAllByTipoPagoIdAndFecha(Integer tipoPagoId, OffsetDateTime fecha);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
