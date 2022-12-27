/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.CarreraNotFoundException;
import ar.edu.um.tesoreria.rest.model.Carrera;
import ar.edu.um.tesoreria.rest.repository.ICarreraRepository;

/**
 * @author daniel
 *
 */
@Service
public class CarreraService {

	@Autowired
	private ICarreraRepository repository;

	public List<Carrera> findAll() {
		return repository.findAll();
	}

	public List<Carrera> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

	public Carrera findByFacultadIdAndPlanIdAndCarreraId(Integer facultadId, Integer planId, Integer carreraId) {
		return repository.findByFacultadIdAndPlanIdAndCarreraId(facultadId, planId, carreraId)
				.orElseThrow(() -> new CarreraNotFoundException(facultadId, planId, carreraId));
	}

	public Carrera add(Carrera carrera) {
		repository.save(carrera);
		return carrera;
	}

	public List<Carrera> saveAll(List<Carrera> carreras) {
		carreras = repository.saveAll(carreras);
		return carreras;
	}

}
