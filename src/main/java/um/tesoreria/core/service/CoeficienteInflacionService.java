/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CoeficienteInflacionException;
import um.tesoreria.core.model.CoeficienteInflacion;
import um.tesoreria.core.repository.ICoeficienteInflacionRepository;

/**
 * @author daniel
 *
 */
@Service
public class CoeficienteInflacionService {

	@Autowired
	private ICoeficienteInflacionRepository repository;

	public List<CoeficienteInflacion> findAll() {
		return repository.findAll();
	}

	public CoeficienteInflacion findByUnique(Integer anho, Integer mes) {
		return repository.findByUnique(anho, mes)
				.orElseThrow(() -> new CoeficienteInflacionException(anho, mes));
	}

	public CoeficienteInflacion add(CoeficienteInflacion coeficienteinflacion) {
		repository.save(coeficienteinflacion);
		return coeficienteinflacion;
	}

	public CoeficienteInflacion update(CoeficienteInflacion newcoeficienteinflacion, Long coeficienteinflacionId) {
		return repository.findById(coeficienteinflacionId).map(coeficienteinflacion -> {
			coeficienteinflacion = new CoeficienteInflacion(coeficienteinflacionId, newcoeficienteinflacion.getAnho(),
					newcoeficienteinflacion.getMes(), newcoeficienteinflacion.getCoeficiente());
			repository.save(coeficienteinflacion);
			return coeficienteinflacion;
		}).orElseThrow(() -> new CoeficienteInflacionException(coeficienteinflacionId));
	}
}
