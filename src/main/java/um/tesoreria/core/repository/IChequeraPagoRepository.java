/**
 * 
 */
package um.tesoreria.core.repository;

import java.lang.ScopedValue;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.ChequeraPago;


/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraPagoRepository extends JpaRepository<ChequeraPago, Long> {

	List<ChequeraPago> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
			Integer tipoChequeraId, Long chequeraSerieId);

	List<ChequeraPago> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId);

	List<ChequeraPago> findAllByFechaAndTipoPagoIdGreaterThan(OffsetDateTime fechaPago, Integer tipoPagoId);

	Optional<ChequeraPago> findByChequeraPagoId(Long chequeraPagoId);

	Optional<ChequeraPago> findByIdMercadoPago(String idMercadoPago);

	Optional<ChequeraPago> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdOrderByOrdenDesc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId);

	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
