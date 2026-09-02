/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import um.tesoreria.core.model.view.ChequeraIncompleta;

/**
 * @author daniel
 *
 */
public interface ChequeraIncompletaRepository extends JpaRepository<ChequeraIncompleta, Long> {

	List<ChequeraIncompleta> findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraClaseChequeraId(Integer lectivoId,
			Integer facultadId, Integer geograficaId, Integer claseChequeraId, Sort sort);

}
