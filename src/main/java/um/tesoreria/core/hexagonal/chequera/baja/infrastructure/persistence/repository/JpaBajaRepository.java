/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.BajaEntity;

/**
 * @author daniel
 *
 */
public interface JpaBajaRepository extends JpaRepository<BajaEntity, Long> {

	public List<BajaEntity> findAllByChequeraIdIn(List<Long> chequeraIds);

	public Optional<BajaEntity> findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId,
	                                                                                Integer tipoChequeraId, Long chequeraSerieId);

	public Optional<BajaEntity> findByBajaId(Long bajaId);

	@Modifying
	public void deleteByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId,
			Long chequeraSerieId);

}
