/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ProvinciaException;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.repository.ProvinciaRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProvinciaService {

	@Autowired
	private ProvinciaRepository repository;

	public List<Provincia> findAllByFacultadId(Integer facultadId) {
		return repository.findAllByFacultadId(facultadId);
	}

	public Provincia findByUniqueId(Long uniqueId) {
		return repository.findByUniqueId(uniqueId).orElseThrow(() -> new ProvinciaException(uniqueId));
	}

	public Provincia findByUnique(Integer facultadId, Integer provinciaId) {
		return repository.findByFacultadIdAndProvinciaId(facultadId, provinciaId)
				.orElseThrow(() -> new ProvinciaException(facultadId, provinciaId));
	}

	public Provincia findByNombre(Integer facultadId, String nombre) {
		return repository.findByFacultadIdAndNombre(facultadId, nombre)
				.orElseThrow(() -> new ProvinciaException(facultadId, nombre));
	}

	public Provincia findLastByFacultadId(Integer facultadId) {
		return repository.findTopByFacultadId(facultadId).orElse(new Provincia(null, facultadId, 0, "", null));
	}

	public Provincia add(Provincia provincia) {
		repository.save(provincia);
		return provincia;
	}

}
