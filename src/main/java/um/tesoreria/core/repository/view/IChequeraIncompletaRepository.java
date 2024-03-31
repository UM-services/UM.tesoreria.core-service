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
public interface IChequeraIncompletaRepository extends JpaRepository<ChequeraIncompleta, Long> {

	public List<ChequeraIncompleta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId,
			Integer facultadId, Integer geograficaId, Sort sort);

}
