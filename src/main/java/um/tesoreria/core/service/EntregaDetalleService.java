/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.exception.EntregaDetalleException;
import um.tesoreria.core.repository.IEntregaDetalleRepository;

/**
 * @author daniel
 *
 */
@Service
public class EntregaDetalleService {

	@Autowired
	private IEntregaDetalleRepository repository;

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

	public EntregaDetalle findByEntregaDetalleId(Long entregaDetalleId) {
		return repository.findByEntregaDetalleId(entregaDetalleId)
				.orElseThrow(() -> new EntregaDetalleException(entregaDetalleId));
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
