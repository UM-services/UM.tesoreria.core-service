/**
 * 
 */
package um.tesoreria.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CursoException;
import um.tesoreria.core.kotlin.model.Curso;
import um.tesoreria.core.repository.ICursoRepository;

/**
 * @author daniel
 *
 */
@Service
public class CursoService {

	@Autowired
	private ICursoRepository repository;

	public Curso findTopByClaseChequera(Integer claseChequeraId) {
		return repository.findTopByClaseChequeraId(claseChequeraId)
				.orElseThrow(() -> new CursoException(claseChequeraId));
	}

}
