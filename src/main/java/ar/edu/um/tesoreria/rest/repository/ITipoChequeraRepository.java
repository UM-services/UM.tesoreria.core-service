/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.TipoChequera;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoChequeraRepository extends JpaRepository<TipoChequera, Integer> {

	public List<TipoChequera> findAllByTipoChequeraIdInAndGeograficaId(List<Integer> tipoChequeraIds,
			Integer geograficaId);

	public List<TipoChequera> findAllByTipoChequeraIdInAndGeograficaIdAndClaseChequeraId(List<Integer> tipoChequeraIds,
			Integer geograficaId, Integer claseChequeraId, Sort sort);

	public List<TipoChequera> findAllByClaseChequeraId(Integer claseChequeraId);

	public Optional<TipoChequera> findTopByOrderByTipoChequeraIdDesc();

	public Optional<TipoChequera> findByTipoChequeraId(Integer tipoChequeraId);

	@Modifying
	public void deleteByTipoChequeraId(Integer tipoChequeraId);

}
