/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.persistence.entity.ChequeraTotalEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaChequeraTotalRepository extends JpaRepository<ChequeraTotalEntity, Long> {

	List<ChequeraTotalEntity> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
	                                                                                 Integer tipoChequeraId, Long chequeraSerieId);

	Optional<ChequeraTotalEntity> findByChequeraTotalId(Long chequeraTotalId);

	Optional<ChequeraTotalEntity> findByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndProductoId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId);

	@Modifying
	void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
