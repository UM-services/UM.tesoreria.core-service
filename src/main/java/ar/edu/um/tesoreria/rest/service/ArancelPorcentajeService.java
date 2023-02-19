/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ArancelPorcentajeException;
import ar.edu.um.tesoreria.rest.model.kotlin.ArancelPorcentaje;
import ar.edu.um.tesoreria.rest.repository.IArancelPorcentajeRepository;

/**
 * @author daniel
 *
 */
@Service
public class ArancelPorcentajeService {
	
	@Autowired
	private IArancelPorcentajeRepository repository;

	public List<ArancelPorcentaje> findAllByArancelTipoID(Integer aranceltipoId) {
		return repository.findAllByAranceltipoId(aranceltipoId);
	}

	public ArancelPorcentaje findByUnique(Integer aranceltipoId, Integer productoId) {
		return repository.findByAranceltipoIdAndProductoId(aranceltipoId, productoId)
				.orElseThrow(() -> new ArancelPorcentajeException(aranceltipoId, productoId));
	}

	public ArancelPorcentaje add(ArancelPorcentaje arancelporcentaje) {
		repository.save(arancelporcentaje);
		return arancelporcentaje;
	}

	public ArancelPorcentaje update(ArancelPorcentaje newarancelporcentaje, Long arancelporcentajeId) {
		return repository.findById(arancelporcentajeId).map(arancelporcentaje -> {
			arancelporcentaje = new ArancelPorcentaje(newarancelporcentaje.getArancelporcentajeId(),
					newarancelporcentaje.getAranceltipoId(), newarancelporcentaje.getProductoId(),
					newarancelporcentaje.getPorcentaje());
			return repository.save(arancelporcentaje);
		}).orElseThrow(() -> new ArancelPorcentajeException(arancelporcentajeId));
	}
}
