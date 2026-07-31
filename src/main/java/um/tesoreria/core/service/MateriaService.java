/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.MateriaException;
import um.tesoreria.core.model.Materia;
import um.tesoreria.core.repository.MateriaRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class MateriaService {

	private final MateriaRepository repository;

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
