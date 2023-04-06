/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorComprobante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.repository.IProveedorComprobanteRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorComprobanteService {

	@Autowired
	private IProveedorComprobanteRepository repository;

	public List<ProveedorComprobante> findAllByOrdenPagoId(Long proveedorMovimientoId) {
		return repository.findAllByProveedorMovimientoIdOrdenPago(proveedorMovimientoId);
	}

}
