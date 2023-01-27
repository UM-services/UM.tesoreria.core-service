/**
 *
 */
package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.exception.ProveedorArticuloException;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.repository.IProveedorArticuloRepository;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ProveedorArticuloService {

	@Autowired
	private IProveedorArticuloRepository repository;

	public List<ProveedorArticulo> findAllByProveedorMovimientoIds(List<Long> proveedorMovimientoIds,
			Boolean asignables) throws JsonProcessingException {
		List<ProveedorArticulo> proveedorArticulos = null;
		if (asignables) {
			proveedorArticulos = repository
					.findAllByProveedorMovimientoIdInAndEntregadoOrderByOrden(proveedorMovimientoIds, BigDecimal.ZERO);
		} else {
			proveedorArticulos = repository.findAllByProveedorMovimientoIdInOrderByOrden(proveedorMovimientoIds);
		}
		log.debug("ProveedorArticulos -> {}", JsonMapper.builder().findAndAddModules().build()
				.writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulos));
		return proveedorArticulos;
	}

	public List<ProveedorArticulo> findAllByProveedorMovimientoId(Long proveedorMovimientoId, Boolean asignables) {
		if (asignables) {
			return repository.findAllByProveedorMovimientoIdAndEntregadoOrderByOrden(proveedorMovimientoId,
					BigDecimal.ZERO);
		}
		return repository.findAllByProveedorMovimientoIdOrderByOrden(proveedorMovimientoId);
	}

	public ProveedorArticulo findByProveedorArticuloId(Long proveedorArticuloId) {
		return repository.findByProveedorArticuloId(proveedorArticuloId)
				.orElseThrow(() -> new ProveedorArticuloException(proveedorArticuloId));
	}

	public ProveedorArticulo update(ProveedorArticulo newProveedorArticulo, Long proveedorArticuloId) {
		return repository.findByProveedorArticuloId(proveedorArticuloId).map(proveedorArticulo -> {
			proveedorArticulo = new ProveedorArticulo(proveedorArticuloId,
					newProveedorArticulo.getProveedorMovimientoId(), newProveedorArticulo.getOrden(),
					newProveedorArticulo.getArticuloId(), newProveedorArticulo.getCantidad(),
					newProveedorArticulo.getConcepto(), newProveedorArticulo.getPrecioUnitario(),
					newProveedorArticulo.getPrecioFinal(), newProveedorArticulo.getEntregado(),
					newProveedorArticulo.getAsignado(), null);
			proveedorArticulo = repository.save(proveedorArticulo);
			return proveedorArticulo;
		}).orElseThrow(() -> new ProveedorArticuloException(proveedorArticuloId));
	}

}
