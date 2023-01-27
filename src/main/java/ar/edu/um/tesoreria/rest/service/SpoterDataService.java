/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.SpoterDataException;
import ar.edu.um.tesoreria.rest.model.SpoterData;
import ar.edu.um.tesoreria.rest.repository.ISpoterDataRepository;

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
