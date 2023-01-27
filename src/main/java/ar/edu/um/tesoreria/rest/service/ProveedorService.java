/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ProveedorException;
import ar.edu.um.tesoreria.rest.model.Proveedor;
import ar.edu.um.tesoreria.rest.model.view.ProveedorSearch;
import ar.edu.um.tesoreria.rest.repository.IProveedorRepository;
import ar.edu.um.tesoreria.rest.service.view.ProveedorSearchService;

/**
 * @author daniel
 *
 */
@Service
public class ProveedorService {

	@Autowired
	private IProveedorRepository repository;

	@Autowired
	private ProveedorSearchService proveedorSearchService;

	public List<Proveedor> findAll() {
		return repository.findAll();
	}

	public List<ProveedorSearch> findAllByStrings(List<String> conditions) {
		return proveedorSearchService.findAllByStrings(conditions);
	}

	public Proveedor findByProveedorId(Integer proveedorId) {
		return repository.findByProveedorId(proveedorId).orElseThrow(() -> new ProveedorException(proveedorId));
	}

	public Proveedor findByCuit(String cuit) {
		return repository.findTopByCuit(cuit).orElseThrow(() -> new ProveedorException(cuit));
	}

	public Proveedor findLast() {
		return repository.findTopByOrderByProveedorIdDesc().orElseThrow(() -> new ProveedorException());
	}

	public Proveedor add(Proveedor proveedor) {
		repository.save(proveedor);
		return proveedor;
	}

	public Proveedor update(Proveedor newProveedor, Integer proveedorId) {
		return repository.findById(proveedorId).map(proveedor -> {
			proveedor = new Proveedor(proveedorId, newProveedor.getCuit(), newProveedor.getNombreFantasia(),
					newProveedor.getRazonSocial(), newProveedor.getOrdenCheque(), newProveedor.getDomicilio(),
					newProveedor.getTelefono(), newProveedor.getFax(), newProveedor.getCelular(),
					newProveedor.getEmail(), newProveedor.getEmailInterno(), newProveedor.getNumeroCuenta(),
					newProveedor.getHabilitado(), null);
			repository.save(proveedor);
			return proveedor;
		}).orElseThrow(() -> new ProveedorException(proveedorId));
	}

	public void delete(Integer proveedorId) {
		repository.deleteById(proveedorId);
	}

}
