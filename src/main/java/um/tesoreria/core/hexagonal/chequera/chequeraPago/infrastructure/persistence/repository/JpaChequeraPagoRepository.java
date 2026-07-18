/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.persistence.entity.ChequeraPagoEntity;


/**
 * @author daniel
 *
 */
@Repository
public interface JpaChequeraPagoRepository extends JpaRepository<ChequeraPagoEntity, Long> {

	List<ChequeraPagoEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
	                                                                                Integer tipoChequeraId, Long chequeraSerieId);

	List<ChequeraPagoEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId);

	List<ChequeraPagoEntity> findAllByFechaBetweenAndTipoPagoIdGreaterThan(OffsetDateTime fechaInicio, OffsetDateTime fechaFin, Integer tipoPagoThreshold);

	List<ChequeraPagoEntity> findAllByTipoPagoIdAndAcreditacion(Integer tipoPagoId, OffsetDateTime fechaAcreditacion);

	List<ChequeraPagoEntity> findAllByTipoPagoIdAndFechaBetween(Integer tipoPagoId, OffsetDateTime fechaInicio, OffsetDateTime fechaFin);

    List<ChequeraPagoEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraCuotaChequeraSerieLectivoId(Integer facultadId, Integer tipoChequeraId, Integer lectivoId);

	Optional<ChequeraPagoEntity> findByChequeraPagoId(Long chequeraPagoId);

	Optional<ChequeraPagoEntity> findByIdMercadoPago(String idMercadoPago);

	Optional<ChequeraPagoEntity> findTopByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoIdAndAlternativaIdAndCuotaIdOrderByOrdenDesc(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId);

	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
