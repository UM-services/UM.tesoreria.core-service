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
	 * Baja todas las líneas de un día/tipo antes de regenerarlas (equivalente al
	 * {@code deleteAll} por pago de {@code clsDAOCheqPagoAsiento}, pero por alcance). El rango
	 * incluye las líneas que quedaron con hora (Mercado Pago previo al 2026-11-21).
	 * Derived delete: cada {@code PostDelete} alimenta el resumen de auditoría.
	 */
	void deleteAllByTipoPagoIdAndFechaGreaterThanEqualAndFechaLessThan(Integer tipoPagoId,
			OffsetDateTime desde, OffsetDateTime hasta);

}
