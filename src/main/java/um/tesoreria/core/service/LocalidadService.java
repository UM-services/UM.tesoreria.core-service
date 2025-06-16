/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.LocalidadException;
import um.tesoreria.core.model.Localidad;
import um.tesoreria.core.repository.LocalidadRepository;

/**
 * @author daniel
 *
 */
@Service
public class LocalidadService {

	@Autowired
	private LocalidadRepository repository;

	public List<Localidad> findAllByProvinciaId(Integer facultadId, Integer provinciaId) {
		return repository.findAllByFacultadIdAndProvinciaId(facultadId, provinciaId);
	}

	public Localidad findByUnique(Integer facultadId, Integer provinciaId, Integer localidadId) {
		return repository.findByFacultadIdAndProvinciaIdAndLocalidadId(facultadId, provinciaId, localidadId)
				.orElseThrow(() -> new LocalidadException(facultadId, provinciaId, localidadId));
	}

	public Localidad findByNombre(Integer facultadId, Integer provinciaId, String nombre) {
		return repository.findByFacultadIdAndProvinciaIdAndNombre(facultadId, provinciaId, nombre)
				.orElseThrow(() -> new LocalidadException(facultadId, provinciaId, nombre));
	}

	public Localidad findLast(Integer facultadId, Integer provinciaId) {
		return repository.findTopByFacultadIdAndProvinciaId(facultadId, provinciaId)
				.orElse(new Localidad(null, facultadId, provinciaId, 0, ""));
	}

	public Localidad add(Localidad localidad) {
		repository.save(localidad);
		return localidad;
	}

}
