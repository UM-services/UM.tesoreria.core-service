/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.FacultadException;
import ar.edu.um.tesoreria.rest.model.Facultad;
import ar.edu.um.tesoreria.rest.model.view.FacultadLectivo;
import ar.edu.um.tesoreria.rest.model.view.FacultadLectivoSede;
import ar.edu.um.tesoreria.rest.model.view.FacultadPersona;
import ar.edu.um.tesoreria.rest.repository.IFacultadRepository;
import ar.edu.um.tesoreria.rest.repository.view.IFacultadLectivoRepository;
import ar.edu.um.tesoreria.rest.repository.view.IFacultadLectivoSedeRepository;
import ar.edu.um.tesoreria.rest.repository.view.IFacultadPersonaRepository;

/**
 * @author daniel
 *
 */
@Service
public class FacultadService {

	@Autowired
	private IFacultadRepository repository;

	@Autowired
	private IFacultadLectivoSedeRepository facultadlectivosederepository;

	@Autowired
	private IFacultadPersonaRepository facultadpersonarepository;

	@Autowired
	private IFacultadLectivoRepository facultadlectivorepository;

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
