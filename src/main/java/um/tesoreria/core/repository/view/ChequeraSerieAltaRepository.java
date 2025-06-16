/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;

/**
 * @author daniel
 *
 */
@Repository
public interface ChequeraSerieAltaRepository extends JpaRepository<ChequeraSerieAlta, Long> {

	public List<ChequeraSerieAlta> findAllByLectivoIdAndFacultadIdAndGeograficaId(Integer lectivoId, Integer facultadId,
			Integer geograficaId);

}
