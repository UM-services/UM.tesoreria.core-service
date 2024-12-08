/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ProveedorException;
import um.tesoreria.core.model.Proveedor;
import um.tesoreria.core.model.view.ProveedorSearch;
import um.tesoreria.core.repository.IProveedorRepository;
import um.tesoreria.core.service.view.ProveedorSearchService;

/**
 * @author daniel
 *
 */
@Service
@Slf4j
public class ProveedorService {

	private final IProveedorRepository repository;
	private final ProveedorSearchService proveedorSearchService;

	public ProveedorService(IProveedorRepository repository,
							ProveedorSearchService proveedorSearchService) {
		this.repository = repository;
		this.proveedorSearchService = proveedorSearchService;
	}

	public List<Proveedor> findAll() {
		log.debug("Processing findAll");
		return repository.findAll();
	}

	public List<ProveedorSearch> findAllByStrings(List<String> conditions) {
		log.debug("Processing findAllByStrings");
		return proveedorSearchService.findAllByStrings(conditions);
	}

	public Proveedor findByProveedorId(Integer proveedorId) {
		log.debug("Processing findByProveedorId");
		return repository.findByProveedorId(proveedorId).orElseThrow(() -> new ProveedorException(proveedorId));
	}

	public Proveedor findByCuit(String cuit) {
		log.debug("Processing findByCuit");
		return repository.findTopByCuit(cuit).orElseThrow(() -> new ProveedorException(cuit));
	}

	public Proveedor findLast() {
		log.debug("Processing findLast");
		return repository.findTopByOrderByProveedorIdDesc().orElseThrow(ProveedorException::new);
	}

	public Proveedor add(Proveedor proveedor) {
		log.debug("Processing add");
		return repository.save(proveedor);
	}

	public Proveedor update(Proveedor newProveedor, Integer proveedorId) {
		log.debug("Processing update");
		return repository.findByProveedorId(proveedorId).map(proveedor -> {
			proveedor = Proveedor.builder()
					.proveedorId(proveedorId)
					.cuit(newProveedor.getCuit())
					.nombreFantasia(newProveedor.getNombreFantasia())
					.razonSocial(newProveedor.getRazonSocial())
					.ordenCheque(newProveedor.getOrdenCheque())
					.domicilio(newProveedor.getDomicilio())
					.telefono(newProveedor.getTelefono())
					.fax(newProveedor.getFax())
					.celular(newProveedor.getCelular())
					.email(newProveedor.getEmail())
					.emailInterno(newProveedor.getEmailInterno())
					.numeroCuenta(newProveedor.getNumeroCuenta())
					.habilitado(newProveedor.getHabilitado())
					.cbu(newProveedor.getCbu())
					.build();
			return repository.save(proveedor);
		}).orElseThrow(() -> new ProveedorException(proveedorId));
	}

	public void delete(Integer proveedorId) {
		log.debug("Processing delete");
		repository.deleteById(proveedorId);
	}

}
