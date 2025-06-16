/**
 * 
 */
package um.tesoreria.core.repository;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.kotlin.model.SpoterData;

/**
 * @author daniel
 *
 */
@Repository
public interface SpoterDataRepository extends JpaRepository<SpoterData, Long> {

	public Optional<SpoterData> findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndGeograficaIdAndLectivoId(
			BigDecimal personaId, Integer documentoId, Integer facultadId, Integer geograficaId, Integer lectivoId);

}
