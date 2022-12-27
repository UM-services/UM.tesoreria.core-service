/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository.view;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ar.edu.um.tesoreria.rest.model.view.ChequeraCuotaPersona;

/**
 * @author daniel
 *
 */
@Repository
public interface IChequeraCuotaPersonaRepository extends JpaRepository<ChequeraCuotaPersona, Long> {

	public Optional<ChequeraCuotaPersona> findTopByPersonaIdAndDocumentoIdAndFacultadIdAndAnhoAndMes(BigDecimal personaId,
			Integer documentoId, Integer facultadId, Integer anho, Integer mes);

}
