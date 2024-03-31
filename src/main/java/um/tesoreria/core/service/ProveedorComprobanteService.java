/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ProveedorComprobante;
import um.tesoreria.core.repository.IProveedorComprobanteRepository;

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
