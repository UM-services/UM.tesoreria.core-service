/**
 * 
 */
package um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.rest.exception.CursoException;
import um.tesoreria.rest.kotlin.model.Curso;
import um.tesoreria.rest.repository.ICursoRepository;
import um.tesoreria.rest.exception.CursoException;
import um.tesoreria.rest.repository.ICursoRepository;

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
