/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.TipoChequeraSede;

/**
 * @author daniel
 *
 */
@Repository
public interface ITipoChequeraSedeRepository extends JpaRepository<TipoChequeraSede, Integer> {

	public List<TipoChequeraSede> findAllByGeograficaId(Integer geograficaId);

	public List<TipoChequeraSede> findAllByTipoChequeraIdInAndClaseChequeraIdIn(List<Integer> tipoChequeraIds,
			List<Integer> claseChequeraIds);

	public Optional<TipoChequeraSede> findByTipoChequeraId(Integer tipoChequeraId);

}
