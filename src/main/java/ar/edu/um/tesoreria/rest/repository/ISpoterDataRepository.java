/**
 * 
 */
package ar.edu.um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import ar.edu.um.tesoreria.rest.kotlin.model.SpoterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author daniel
 *
 */
@Repository
public interface ISpoterDataRepository extends JpaRepository<SpoterData, Long> {

	public Optional<SpoterData> findTopByPersonaIdAndDocumentoIdAndFacultadIdAndGeograficaIdAndLectivoId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer geograficaId, Integer lectivoId);

}
