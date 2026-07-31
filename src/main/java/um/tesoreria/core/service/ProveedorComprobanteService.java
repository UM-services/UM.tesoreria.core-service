/**
 * 
 */
package um.tesoreria.core.service;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.stereotype.Service;

import um.tesoreria.core.kotlin.model.ProveedorComprobante;
import um.tesoreria.core.repository.ProveedorComprobanteRepository;

/**
 * @author daniel
 *
 */
@Service
@RequiredArgsConstructor
public class ProveedorComprobanteService {

	private final ProveedorComprobanteRepository repository;

	public List<ProveedorComprobante> findAllByOrdenPagoId(Long proveedorMovimientoId) {
		return repository.findAllByProveedorMovimientoIdOrdenPago(proveedorMovimientoId);
	}

}
