/**
 * 
 */
package um.tesoreria.rest.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.rest.kotlin.model.SpoterData;

/**
 * @author daniel
 *
 */
@Repository
public interface ISpoterDataRepository extends JpaRepository<SpoterData, Long> {

	public Optional<SpoterData> findTopByPersonaIdAndDocumentoIdAndFacultadIdAndGeograficaIdAndLectivoId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer geograficaId, Integer lectivoId);

}
