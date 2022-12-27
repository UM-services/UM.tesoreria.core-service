/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.CursoNotFoundException;
import ar.edu.um.tesoreria.rest.model.Curso;
import ar.edu.um.tesoreria.rest.repository.ICursoRepository;

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
				.orElseThrow(() -> new CursoNotFoundException(claseChequeraId));
	}

}
