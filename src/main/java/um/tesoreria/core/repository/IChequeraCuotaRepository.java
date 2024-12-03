/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.core.kotlin.model.ChequeraCuota;

/**
 * @author daniel
 *
 */
public interface IChequeraCuotaRepository extends JpaRepository<ChequeraCuota, Long> {

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Sort sort);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja,
			Byte pagado, OffsetDateTime referencia, BigDecimal importe);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, BigDecimal importe1,
			Sort sort);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte baja);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte pagado);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBajaAndImporte1GreaterThan(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte pagado, Byte baja, BigDecimal zero);

	List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte pagado, Byte baja);

	Optional<ChequeraCuota> findByChequeraCuotaId(Long chequeraCuotaId);

	Optional<ChequeraCuota> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId,
			Integer cuotaId);

	Optional<ChequeraCuota> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndImporte1GreaterThanAndVencimiento1LessThanOrderByVencimiento1Desc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja, Byte pagado, BigDecimal importe1, OffsetDateTime vencimiento1);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
