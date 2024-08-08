/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CarreraException;
import um.tesoreria.core.kotlin.model.Carrera;
import um.tesoreria.core.repository.ICarreraRepository;

/**
 * @author daniel
 *
 */
@Service
public class CarreraService {

	private final ICarreraRepository repository;

	public CarreraService(ICarreraRepository repository) {
		this.repository = repository;
	}

	public List<Carrera> findAll() {
		return repository.findAll();
	}

	public List<Carrera> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

	public Carrera findByFacultadIdAndPlanIdAndCarreraId(Integer facultadId, Integer planId, Integer carreraId) {
		return repository.findByFacultadIdAndPlanIdAndCarreraId(facultadId, planId, carreraId)
				.orElseThrow(() -> new CarreraException(facultadId, planId, carreraId));
	}

	public Carrera add(Carrera carrera) {
		return repository.save(carrera);
	}

	public List<Carrera> saveAll(List<Carrera> carreras) {
		return repository.saveAll(carreras);
	}

}
