/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.model.Legajo;
import um.tesoreria.core.repository.ILegajoRepository;

/**
 * @author daniel
 *
 */
@Service
public class LegajoService {

	@Autowired
	private ILegajoRepository repository;

	public Legajo findByFacultadIdAndPersonaIdAndDocumentoId(Integer facultadId, BigDecimal personaId,
			Integer documentoId) {
		return repository.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId)
				.orElseThrow(() -> new LegajoException(facultadId, personaId, documentoId));
	}

	public List<Legajo> saveAll(List<Legajo> legajos) {
		legajos = repository.saveAll(legajos);
		return legajos;
	}

	public Legajo add(Legajo legajo) {
		repository.save(legajo);
		return legajo;
	}

}
