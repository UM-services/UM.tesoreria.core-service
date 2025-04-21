/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.kotlin.model.Legajo;
import um.tesoreria.core.repository.LegajoRepository;

/**
 * @author daniel
 *
 */
@Service
public class LegajoService {

	private final LegajoRepository repository;

	public LegajoService(LegajoRepository repository) {
		this.repository = repository;
	}

	public List<Legajo> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

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
		legajo =repository.save(legajo);
		return legajo;
	}

}
