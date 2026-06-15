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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import um.tesoreria.core.kotlin.model.ChequeraCuotaEntity;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;

/**
 * @author daniel
 *
 */
public interface ChequeraCuotaRepository extends JpaRepository<ChequeraCuotaEntity, Long> {

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Sort sort);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndVencimiento1LessThanEqualAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja,
			Byte pagado, OffsetDateTime referencia, BigDecimal importe);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, BigDecimal importe1,
			Sort sort);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndImporte1GreaterThan(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja, BigDecimal importe1,
			Sort sort);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte baja);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndPagado(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Byte pagado);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBajaAndCompensadaAndImporte1GreaterThan(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte pagado, Byte baja, Byte compensada, BigDecimal zero);

	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndPagadoAndBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte pagado, Byte baja);

	Optional<ChequeraCuotaEntity> findByChequeraCuotaId(Long chequeraCuotaId);

	Optional<ChequeraCuotaEntity> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(
			Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId,
			Integer cuotaId);

	Optional<ChequeraCuotaEntity> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndBajaAndPagadoAndImporte1GreaterThanAndVencimiento1LessThanOrderByVencimiento1Desc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Byte baja, Byte pagado, BigDecimal importe1, OffsetDateTime vencimiento1);

	Optional<ChequeraCuotaEntity> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdAndCuotaIdOrderByProductoIdDesc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, Integer cuotaId);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

	@Query("SELECT new um.tesoreria.core.model.internal.CuotaPeriodoDto(cc.mes, cc.anho, COUNT(*)) " +
		   "FROM ChequeraCuotaEntity cc " +
		   "JOIN ChequeraSerieEntity cs ON cc.chequeraId = cs.chequeraId " +
		   "WHERE cs.lectivoId = :lectivoId " +
		   "AND cc.importe1 > 0 " +
		   "GROUP BY cc.mes, cc.anho " +
		   "ORDER BY cc.anho, cc.mes")
	List<CuotaPeriodoDto> findCuotaPeriodosByLectivoId(@Param("lectivoId") Integer lectivoId);

	@Query("SELECT c FROM ChequeraCuotaEntity c LEFT JOIN FETCH c.producto WHERE c.chequeraId IN :chequeraIds")
	List<ChequeraCuotaEntity> findAllByChequeraIdIn(@Param("chequeraIds") List<Long> chequeraIds);

	@Query("SELECT c FROM ChequeraCuotaEntity c LEFT JOIN FETCH c.producto WHERE c.facultadId = :facultadId AND c.tipoChequeraId = :tipoChequeraId AND c.chequeraSerieId IN :chequeraSerieIds")
	List<ChequeraCuotaEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdIn(@Param("facultadId") Integer facultadId, @Param("tipoChequeraId") Integer tipoChequeraId, @Param("chequeraSerieIds") List<Long> chequeraSerieIds);

}
