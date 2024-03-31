/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import um.tesoreria.core.model.view.ChequeraPreuniv;

/**
 * @author daniel
 *
 */
@Service
public interface IChequeraPreunivRepository extends JpaRepository<ChequeraPreuniv, Long> {

	public List<ChequeraPreuniv> findAllByFacultadIdAndLectivoIdAndGeograficaIdAndPersonaKeyIn(Integer facultadId,
			Integer lectivoId, Integer geograficaId, List<String> personaKeys);

	public List<ChequeraPreuniv> findAllByFacultadIdAndLectivoId(Integer facultadId, Integer lectivoId);

}
