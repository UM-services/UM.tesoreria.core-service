/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.model.ProveedorComprobante;
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
