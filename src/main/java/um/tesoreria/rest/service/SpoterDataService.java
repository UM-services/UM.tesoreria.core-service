/**
 * 
 */
package um.tesoreria.rest.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.SpoterDataException;
import um.tesoreria.rest.repository.ISpoterDataRepository;
import um.tesoreria.rest.kotlin.model.SpoterData;

/**
 * @author daniel
 *
 */
@Service
public class SpoterDataService {

	@Autowired
	private ISpoterDataRepository repository;

	public SpoterData findExistentRequest(BigDecimal personaId, Integer documentoId, Integer facultadId,
                                          Integer geograficaId, Integer lectivoId) {
		return repository
				.findTopByPersonaIdAndDocumentoIdAndFacultadIdAndGeograficaIdAndLectivoId(personaId, documentoId,
						facultadId, geograficaId, lectivoId)
				.orElseThrow(() -> new SpoterDataException(personaId, documentoId, facultadId, geograficaId,
						lectivoId));
	}

	public SpoterData add(SpoterData spoterData) {
		spoterData = repository.save(spoterData);
		return spoterData;
	}

}
