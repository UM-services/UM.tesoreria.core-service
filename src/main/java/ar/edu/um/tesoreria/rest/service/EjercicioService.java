/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.EjercicioNotFoundException;
import ar.edu.um.tesoreria.rest.model.Ejercicio;
import ar.edu.um.tesoreria.rest.repository.IEjercicioRepository;

/**
 * @author daniel
 *
 */
@Service
public class EjercicioService {
	@Autowired
	private IEjercicioRepository repository;

	public List<Ejercicio> findAll() {
		return repository.findAll(Sort.by("ejercicioId").descending());
	}

	public Ejercicio findByEjercicioId(Integer ejercicioId) {
		return repository.findByEjercicioId(ejercicioId).orElseThrow(() -> new EjercicioNotFoundException(ejercicioId));
	}

	public Ejercicio findByFecha(OffsetDateTime fecha) {
		return repository.findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(fecha, fecha)
				.orElseThrow(() -> new EjercicioNotFoundException(fecha));
	}

	public Ejercicio findLast() {
		return repository.findTopByOrderByEjercicioIdDesc().orElseThrow(() -> new EjercicioNotFoundException("Last"));
	}

	public Ejercicio add(Ejercicio ejercicio) {
		repository.save(ejercicio);
		return ejercicio;
	}

	public Ejercicio update(Ejercicio newEjercicio, Integer ejercicioId) {
		return repository.findByEjercicioId(ejercicioId).map(ejercicio -> {
			ejercicio = new Ejercicio(ejercicioId, newEjercicio.getNombre(), newEjercicio.getFechaInicio(),
					newEjercicio.getFechaFinal(), newEjercicio.getBloqueado(), newEjercicio.getOrdenContableResultado(),
					newEjercicio.getOrdenContableBienesUso());
			ejercicio = repository.save(ejercicio);
			return ejercicio;
		}).orElseThrow(() -> new EjercicioNotFoundException(ejercicioId));
	}

	public void delete(Integer ejercicioId) {
		repository.deleteById(ejercicioId);
	}

}
