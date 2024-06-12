/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.DependenciaException;
import um.tesoreria.core.kotlin.model.Dependencia;
import um.tesoreria.core.repository.IDependenciaRepository;

/**
 * @author daniel
 *
 */
@Service
public class DependenciaService {
	
	private final IDependenciaRepository repository;

	@Autowired
	public DependenciaService(IDependenciaRepository repository) {
		this.repository = repository;
	}

	public List<Dependencia> findAll() {
		return repository.findAllByOrderByNombre();
	}

    public Dependencia findByDependenciaId(Integer dependenciaId) {
		return repository.findByDependenciaId(dependenciaId).orElseThrow(() -> new DependenciaException(dependenciaId));
    }

	public Dependencia update(Integer dependenciaId, Dependencia newDependencia) {
		return repository.findByDependenciaId(dependenciaId).map(dependencia -> {
			dependencia = new Dependencia.Builder()
					.dependenciaId(dependenciaId)
					.nombre(newDependencia.getNombre())
					.acronimo(newDependencia.getAcronimo())
					.facultadId(newDependencia.getFacultadId())
					.geograficaId(newDependencia.getGeograficaId())
					.cuentaHonorariosPagar(newDependencia.getCuentaHonorariosPagar())
					.build();
			return repository.save(dependencia);
		}).orElseThrow(() -> new DependenciaException(dependenciaId));
	}
}
