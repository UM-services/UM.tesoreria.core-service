/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.FacultadPersona;

/**
 * @author daniel
 *
 */
@Repository
public interface IFacultadPersonaRepository extends JpaRepository<FacultadPersona, Long> {

	public List<FacultadPersona> findAllByPersonaIdAndDocumentoIdAndLectivoId(BigDecimal personaId, Integer documentoId,
			Integer lectivoId);

}
