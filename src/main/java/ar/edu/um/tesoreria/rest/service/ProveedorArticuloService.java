/**
 *
 */
package ar.edu.um.tesoreria.rest.service;

import ar.edu.um.tesoreria.rest.exception.ProveedorArticuloNotFoundException;
import ar.edu.um.tesoreria.rest.model.ProveedorArticulo;
import ar.edu.um.tesoreria.rest.repository.IProveedorArticuloRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author daniel
 */
@Service
public class ProveedorArticuloService {

	@Autowired
	private IProveedorArticuloRepository repository;

	public List<ProveedorArticulo> findAllByProveedorMovimientoIds(List<Long> proveedorMovimientoIds,
			Boolean asignables) {
		if (asignables) {
			return repository.findAllByProveedorMovimientoIdInAndEntregadoOrderByOrden(proveedorMovimientoIds,
					BigDecimal.ZERO);
		}
		return repository.findAllByProveedorMovimientoIdInOrderByOrden(proveedorMovimientoIds);
	}

	public List<ProveedorArticulo> findAllByProveedorMovimientoId(Long proveedorMovimientoId, Boolean asignables) {
		if (asignables) {
			return repository.findAllByProveedorMovimientoIdAndEntregadoOrderByOrden(proveedorMovimientoId,
					BigDecimal.ZERO);
		}
		return repository.findAllByProveedorMovimientoIdOrderByOrden(proveedorMovimientoId);
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
		}).orElseThrow(() -> new ProveedorArticuloNotFoundException(proveedorArticuloId));
	}

}
