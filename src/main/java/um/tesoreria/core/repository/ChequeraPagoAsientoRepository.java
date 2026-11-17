/**
 * 
 */
package um.tesoreria.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 *
 */
public interface ChequeraPagoAsientoRepository extends JpaRepository<ChequeraPagoAsiento, Long> {

	List<ChequeraPagoAsiento> findAllByTipoPagoIdAndFecha(Integer tipoPagoId, OffsetDateTime fecha);

	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

	/**
	 * Baja las líneas de un pago puntual (clave compuesta del legacy, sin {@code item}) antes de
	 * regenerarlas. Es el equivalente derivado de {@code clsDAOCheqPagoAsiento.deleteAll}.
	 */
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdAndOrden(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId,
			Integer alternativaId, Integer cuotaId, Integer orden);

}
