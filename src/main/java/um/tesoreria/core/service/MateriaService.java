/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.MateriaException;
import um.tesoreria.core.model.Materia;
import um.tesoreria.core.repository.IMateriaRepository;

/**
 * @author daniel
 *
 */
@Service
public class MateriaService {

	@Autowired
	private IMateriaRepository repository;

	public List<Materia> findAll() {
		return repository.findAll();
	}

	public List<Materia> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

	public Materia findByUnique(Integer facultadId, Integer planId, String materiaId) {
		return repository.findByFacultadIdAndPlanIdAndMateriaId(facultadId, planId, materiaId)
				.orElseThrow(() -> new MateriaException(facultadId, planId, materiaId));
	}

}
