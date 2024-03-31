/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.SpoterDataException;
import um.tesoreria.core.repository.ISpoterDataRepository;
import um.tesoreria.core.kotlin.model.SpoterData;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class SpoterDataService {

	private final ISpoterDataRepository repository;

	@Autowired
	public SpoterDataService(ISpoterDataRepository repository) {
		this.repository = repository;
	}

	public SpoterData findOne(BigDecimal personaId, Integer documentoId, Integer facultadId,
													Integer geograficaId, Integer lectivoId) {
		log.info("Recovering Spoter: {}/{}/{}/{}/{}", personaId, documentoId, facultadId, geograficaId, lectivoId);
		return repository
				.findFirstByPersonaIdAndDocumentoIdAndFacultadIdAndGeograficaIdAndLectivoId(personaId, documentoId,
						facultadId, geograficaId, lectivoId)
				.orElseThrow(() -> new SpoterDataException(personaId, documentoId, facultadId, geograficaId,
						lectivoId));
	}

	public SpoterData add(SpoterData spoterData) {
		spoterData = repository.save(spoterData);
		return spoterData;
	}

}
