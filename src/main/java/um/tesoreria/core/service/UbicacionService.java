/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;
import java.util.stream.Collectors;

import um.tesoreria.core.kotlin.model.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.repository.UbicacionRepository;

/**
 * @author daniel
 *
 */
@Service
public class UbicacionService {

	private final UbicacionRepository repository;

	@Autowired
	public UbicacionService(UbicacionRepository repository) {
		this.repository = repository;
	}

	public List<Ubicacion> findAll() {
		return repository.findAll();
	}

	public List<Ubicacion> findAllBySede(Integer geograficaId) {
		List<Ubicacion> ubicacions = repository.findAllByDependenciaIdNotNull();
		return ubicacions.stream().filter(ubicacion -> ubicacion.getDependencia().getGeograficaId() == geograficaId)
				.collect(Collectors.toList());
	}

}
