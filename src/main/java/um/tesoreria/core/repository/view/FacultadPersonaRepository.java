/**
 * 
 */
package um.tesoreria.core.repository.view;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.model.view.FacultadPersona;

/**
 * @author daniel
 *
 */
@Repository
public interface FacultadPersonaRepository extends JpaRepository<FacultadPersona, Long> {

	public List<FacultadPersona> findAllByPersonaIdAndDocumentoIdAndLectivoId(BigDecimal personaId, Integer documentoId,
			Integer lectivoId);

}
