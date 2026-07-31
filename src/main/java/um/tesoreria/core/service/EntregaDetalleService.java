/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.exception.EntregaDetalleException;
import um.tesoreria.core.repository.EntregaDetalleRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class EntregaDetalleService {

	private final EntregaDetalleRepository repository;

	public List<EntregaDetalle> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
		return repository.findAllByProveedorMovimientoIdOrderByOrden(proveedorMovimientoId);
	}

	public List<EntregaDetalle> findAllByProveedorMovimientoIds(List<Long> proveedorMovimientoIds) {
		return repository.findAllByProveedorMovimientoIdInOrderByOrden(proveedorMovimientoIds);
	}

	public List<EntregaDetalle> findAllByEntregaId(Long entregaId) {
		return repository.findAllByEntregaIdOrderByOrden(entregaId);
	}

	public List<EntregaDetalle> findAllByProveedorMovimientoIdAndOrden(Long proveedorMovimientoId, Integer orden) {
		return repository.findAllByProveedorMovimientoIdAndOrden(proveedorMovimientoId, orden);
	}

	public List<EntregaDetalle> findAllByProveedorArticuloId(Long proveedorArticuloId) {
		return repository.findAllByProveedorArticuloId(proveedorArticuloId);
	}

	public EntregaDetalle findByEntregaDetalleId(Long entregaDetalleId) {
		return repository.findByEntregaDetalleId(entregaDetalleId)
				.orElseThrow(() -> new EntregaDetalleException(entregaDetalleId, "entregaDetalleId"));
	}

	public EntregaDetalle add(EntregaDetalle entregaDetalle) {
		entregaDetalle = repository.save(entregaDetalle);
		return entregaDetalle;
	}

	@Transactional
	public void deleteByEntregaDetalleId(Long entregaDetalleId) {
		repository.deleteByEntregaDetalleId(entregaDetalleId);
	}

}
