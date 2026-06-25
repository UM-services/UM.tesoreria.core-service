/**
 * 
 */
package um.tesoreria.core.hexagonal.arancelPorcentaje.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.hexagonal.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.arancelPorcentaje.infrastructure.persistence.entity.ArancelPorcentajeEntity;
import um.tesoreria.core.hexagonal.arancelPorcentaje.infrastructure.persistence.repository.JpaArancelPorcentajeRepository;

/**
 * @author daniel
 *
 */
@Service
public class ArancelPorcentajeService {
	
	@Autowired
	private JpaArancelPorcentajeRepository repository;

	public List<ArancelPorcentajeEntity> findAllByArancelTipoID(Integer aranceltipoId) {
		return repository.findAllByAranceltipoId(aranceltipoId);
	}

	public ArancelPorcentajeEntity findByUnique(Integer aranceltipoId, Integer productoId) {
		return repository.findByAranceltipoIdAndProductoId(aranceltipoId, productoId)
				.orElseThrow(() -> new ArancelPorcentajeException(aranceltipoId, productoId));
	}

	public ArancelPorcentajeEntity add(ArancelPorcentajeEntity arancelporcentaje) {
		repository.save(arancelporcentaje);
		return arancelporcentaje;
	}

	public ArancelPorcentajeEntity update(ArancelPorcentajeEntity newarancelporcentaje, Long arancelporcentajeId) {
		return repository.findById(arancelporcentajeId).map(arancelporcentaje -> {
			arancelporcentaje = new ArancelPorcentajeEntity(newarancelporcentaje.getArancelporcentajeId(),
					newarancelporcentaje.getAranceltipoId(), newarancelporcentaje.getProductoId(),
					newarancelporcentaje.getPorcentaje());
			return repository.save(arancelporcentaje);
		}).orElseThrow(() -> new ArancelPorcentajeException(arancelporcentajeId));
	}
}
