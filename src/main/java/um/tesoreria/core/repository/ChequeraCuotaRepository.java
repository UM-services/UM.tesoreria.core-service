/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;

/**
 * @author daniel
 *
 */
public interface ChequeraCuotaRepository extends JpaRepository<ChequeraCuota, Long> {

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

	Optional<ChequeraCuota> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndCuotaIdOrderByProductoIdDesc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer cuotaId);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

	@Query("SELECT new um.tesoreria.core.model.internal.CuotaPeriodoDto(cc.productoId, cc.mes, cc.anho, COUNT(*)) " +
		   "FROM ChequeraCuota cc " +
		   "JOIN ChequeraSerie cs ON cc.chequeraId = cs.chequeraId " +
		   "WHERE cs.lectivoId = :lectivoId " +
		   "AND cc.importe1 > 0 " +
		   "GROUP BY cc.productoId, cc.mes, cc.anho " +
		   "ORDER BY cc.anho, cc.mes, cc.productoId")
	List<CuotaPeriodoDto> findCuotaPeriodosByLectivoId(@Param("lectivoId") Integer lectivoId);

}
