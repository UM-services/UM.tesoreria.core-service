/**
 * 
 */
package um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.rest.kotlin.model.ChequeraCuota;

/**
 * @author daniel
 *
 */
public interface IChequeraCuotaRepository extends JpaRepository<ChequeraCuota, Long> {

	public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBajaAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer pagado,
			Integer baja, BigDecimal importe);

	public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Sort sort);

	public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja,
			Byte pagado, OffsetDateTime referencia, BigDecimal importe);

	public List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, BigDecimal importe1,
			Sort sort);

	public Optional<ChequeraCuota> findByChequeraCuotaId(Long chequeraCuotaId);

	public Optional<ChequeraCuota> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId,
			Integer cuotaId);

	public Optional<ChequeraCuota> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndImporte1GreaterThanAndVencimiento1LessThanOrderByVencimiento1Desc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja, Byte pagado, BigDecimal importe1, OffsetDateTime vencimiento1);

	@Modifying
	public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
