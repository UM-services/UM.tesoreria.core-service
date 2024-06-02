/**
 *
 */
package um.tesoreria.core.service;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.exception.ProveedorArticuloException;
import um.tesoreria.core.repository.IProveedorArticuloRepository;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author daniel
 */
@Service
@Slf4j
public class ProveedorArticuloService {

	private final IProveedorArticuloRepository repository;

	@Autowired
	public ProveedorArticuloService(IProveedorArticuloRepository repository) {
		this.repository = repository;
	}

	public List<ProveedorArticulo> findAllByProveedorMovimientoIds(List<Long> proveedorMovimientoIds,
                                                                   Boolean asignables) {
		List<ProveedorArticulo> proveedorArticulos = null;
		if (asignables) {
			proveedorArticulos = repository
					.findAllByProveedorMovimientoIdInAndEntregadoOrderByOrden(proveedorMovimientoIds, BigDecimal.ZERO);
		} else {
			proveedorArticulos = repository.findAllByProveedorMovimientoIdInOrderByOrden(proveedorMovimientoIds);
		}
        try {
            log.debug("ProveedorArticulos -> {}", JsonMapper.builder().findAndAddModules().build().writerWithDefaultPrettyPrinter().writeValueAsString(proveedorArticulos));
        } catch (JsonProcessingException e) {
            log.debug("ProveedorArticulos -> error: {}", e.getMessage());
        }
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

	@Transactional
    public void deleteByProveedorArticuloId(Long proveedorArticuloId) {
		repository.deleteByProveedorArticuloId(proveedorArticuloId);
    }
}
