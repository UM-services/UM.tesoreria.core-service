/**
 * 
 */
package um.tesoreria.core.service;

import java.time.OffsetDateTime;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.EjercicioException;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.repository.EjercicioRepository;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class EjercicioService {

	private final EjercicioRepository repository;

	public EjercicioService(EjercicioRepository repository) {
		this.repository = repository;
	}

	public List<Ejercicio> findAll() {
		return repository.findAll(Sort.by("ejercicioId").descending());
	}

	public Ejercicio findByEjercicioId(Integer ejercicioId) {
		return repository.findByEjercicioId(ejercicioId).orElseThrow(() -> new EjercicioException(ejercicioId));
	}

	public Ejercicio findByFecha(OffsetDateTime fecha) {
		log.debug("Processing Ejercicio findByFecha -> {}", fecha);
		return repository.findByFechaInicioLessThanEqualAndFechaFinalGreaterThanEqual(fecha, fecha)
				.map(ejercicio -> {
                    try {
                        log.debug("Ejercicio findByFecha -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(ejercicio));
                    } catch (JsonProcessingException e) {
                        log.debug("Ejercicion jsonify error");
                    }
                    return ejercicio;
				})
				.orElseThrow(() -> new EjercicioException(fecha));
	}

	public Ejercicio findLast() {
		return repository.findTopByOrderByEjercicioIdDesc().orElseThrow(() -> new EjercicioException("Last"));
	}

}
