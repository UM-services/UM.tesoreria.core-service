/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CursoException;
import um.tesoreria.core.kotlin.model.Curso;
import um.tesoreria.core.repository.ICursoRepository;

import java.util.List;

/**
 * @author daniel
 *
 */
@Service
public class CursoService {

	private final ICursoRepository repository;

	public CursoService(ICursoRepository repository) {
		this.repository = repository;
	}

	public List<Curso> findAll() {
		return repository.findAll();
	}

	public Curso findByCursoId(Integer cursoId) {
		return repository.findByCursoId(cursoId)
				.orElseThrow(() -> new CursoException(cursoId));
	}

	public Curso findTopByClaseChequera(Integer claseChequeraId) {
		return repository.findTopByClaseChequeraId(claseChequeraId)
				.orElseThrow(() -> new CursoException(claseChequeraId));
	}

}
