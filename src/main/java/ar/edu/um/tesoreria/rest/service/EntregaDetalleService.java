/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.EntregaDetalle;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.repository.EntregaDetalleException;
import ar.edu.um.tesoreria.rest.repository.IEntregaDetalleRepository;

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
