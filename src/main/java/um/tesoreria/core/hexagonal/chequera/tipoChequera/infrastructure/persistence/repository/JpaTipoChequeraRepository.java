/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.persistence.entity.TipoChequeraEntity;

/**
 * @author daniel
 *
 */
@Repository
public interface JpaTipoChequeraRepository extends JpaRepository<TipoChequeraEntity, Integer> {

	public List<TipoChequeraEntity> findAllByTipoChequeraIdInAndGeograficaId(List<Integer> tipoChequeraIds,
                                                                             Integer geograficaId);

	public List<TipoChequeraEntity> findAllByTipoChequeraIdInAndGeograficaIdAndClaseChequeraId(List<Integer> tipoChequeraIds,
                                                                                               Integer geograficaId, Integer claseChequeraId, Sort sort);

	public List<TipoChequeraEntity> findAllByClaseChequeraId(Integer claseChequeraId);

	public List<TipoChequeraEntity> findAllByClaseChequeraIdIn(List<Integer> claseChequeraIds);

	public Optional<TipoChequeraEntity> findTopByOrderByTipoChequeraIdDesc();

	public Optional<TipoChequeraEntity> findByTipoChequeraId(Integer tipoChequeraId);

	@Modifying
	public void deleteByTipoChequeraId(Integer tipoChequeraId);

}
