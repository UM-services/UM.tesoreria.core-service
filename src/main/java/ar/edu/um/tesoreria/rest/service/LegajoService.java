/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.LegajoException;
import ar.edu.um.tesoreria.rest.model.Legajo;
import ar.edu.um.tesoreria.rest.repository.ILegajoRepository;

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
