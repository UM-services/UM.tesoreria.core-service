/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.CoeficienteInflacionException;
import um.tesoreria.core.model.CoeficienteInflacion;
import um.tesoreria.core.repository.CoeficienteInflacionRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class CoeficienteInflacionService {

	private final CoeficienteInflacionRepository repository;

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
