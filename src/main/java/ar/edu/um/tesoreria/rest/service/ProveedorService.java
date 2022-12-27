/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ProveedorNotFoundException;
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
	private ProveedorSearchService proveedorsearchservice;

	public List<Proveedor> findAll() {
		return repository.findAll();
	}

	public List<ProveedorSearch> findByStrings(List<String> conditions) {
		return proveedorsearchservice.findAllByStrings(conditions);
	}

	public Proveedor findByProveedorId(Integer proveedorId) {
		return repository.findByProveedorId(proveedorId).orElseThrow(() -> new ProveedorNotFoundException(proveedorId));
	}

	public Proveedor findByCuit(String cuit) {
		return repository.findTopByCuit(cuit).orElseThrow(() -> new ProveedorNotFoundException(cuit));
	}

	public Proveedor findLast() {
		return repository.findTopByOrderByProveedorIdDesc().orElseThrow(() -> new ProveedorNotFoundException());
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
		}).orElseThrow(() -> new ProveedorNotFoundException(proveedorId));
	}

	public void delete(Integer proveedorId) {
		repository.deleteById(proveedorId);
	}

}
