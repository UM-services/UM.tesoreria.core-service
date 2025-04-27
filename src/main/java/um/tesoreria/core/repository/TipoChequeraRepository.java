/**
 * 
 */
package um.tesoreria.core.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.TipoChequera;

/**
 * @author daniel
 *
 */
@Repository
public interface TipoChequeraRepository extends JpaRepository<TipoChequera, Integer> {

	public List<TipoChequera> findAllByTipoChequeraIdInAndGeograficaId(List<Integer> tipoChequeraIds,
			Integer geograficaId);

	public List<TipoChequera> findAllByTipoChequeraIdInAndGeograficaIdAndClaseChequeraId(List<Integer> tipoChequeraIds,
			Integer geograficaId, Integer claseChequeraId, Sort sort);

	public List<TipoChequera> findAllByClaseChequeraId(Integer claseChequeraId);

	public List<TipoChequera> findAllByClaseChequeraIdIn(List<Integer> claseChequeraIds);

	public Optional<TipoChequera> findTopByOrderByTipoChequeraIdDesc();

	public Optional<TipoChequera> findByTipoChequeraId(Integer tipoChequeraId);

	@Modifying
	public void deleteByTipoChequeraId(Integer tipoChequeraId);

}
