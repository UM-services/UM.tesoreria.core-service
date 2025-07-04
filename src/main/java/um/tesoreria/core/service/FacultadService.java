/**
 * 
 */
package um.tesoreria.core.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.model.view.FacultadLectivo;
import um.tesoreria.core.model.view.FacultadLectivoSede;
import um.tesoreria.core.model.view.FacultadPersona;
import um.tesoreria.core.repository.FacultadRepository;
import um.tesoreria.core.repository.view.FacultadLectivoRepository;
import um.tesoreria.core.repository.view.FacultadLectivoSedeRepository;
import um.tesoreria.core.repository.view.FacultadPersonaRepository;

/**
 * @author daniel
 *
 */
@Service
public class FacultadService {

	@Autowired
	private FacultadRepository repository;

	@Autowired
	private FacultadLectivoSedeRepository facultadlectivosederepository;

	@Autowired
	private FacultadPersonaRepository facultadpersonarepository;

	@Autowired
	private FacultadLectivoRepository facultadlectivorepository;

	public List<Facultad> findAll() {
		return repository.findAll(Sort.by("facultadId").ascending());
	}

	public List<Facultad> findFacultades() {
		Integer[] facultades = { 1, 2, 3, 4, 5, 14, 15 };
		return repository.findAllByFacultadIdIn(Arrays.asList(facultades));
	}

	public List<FacultadLectivo> findAllByLectivoId(Integer lectivoId) {
		Integer[] facultades = { 1, 2, 3, 4, 5, 14, 15 };
		return facultadlectivorepository.findAllByLectivoIdAndFacultadIdIn(lectivoId, Arrays.asList(facultades));
	}

	public List<FacultadPersona> findAllByPersona(BigDecimal personaId, Integer documentoId, Integer lectivoId) {
		return facultadpersonarepository.findAllByPersonaIdAndDocumentoIdAndLectivoId(personaId, documentoId,
				lectivoId);
	}

	public List<FacultadLectivoSede> findAllByDisenho(Integer lectivoId, Integer geograficaId) {
		if (geograficaId == 0)
			return facultadlectivosederepository.findAllByLectivoId(lectivoId);
		return facultadlectivosederepository.findAllByLectivoIdAndGeograficaId(lectivoId, geograficaId);
	}

	public Facultad findByFacultadId(Integer facultadId) {
		return repository.findByFacultadId(facultadId).orElseThrow(() -> new FacultadException(facultadId));
	}

}
