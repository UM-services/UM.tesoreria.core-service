/**
 * 
 */
package um.tesoreria.core.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import um.tesoreria.core.exception.ProductoException;
import um.tesoreria.core.kotlin.model.Producto;
import um.tesoreria.core.model.view.ProductoTipoChequera;
import um.tesoreria.core.repository.ProductoRepository;
import um.tesoreria.core.repository.view.ProductoTipoChequeraRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProductoService {

	@Autowired
	private ProductoRepository repository;
	
	@Autowired
	private ProductoTipoChequeraRepository productotipochequerarepository;

	public List<Producto> findAll() {
		return repository.findAll();
	}

	public List<ProductoTipoChequera> findAllByTipoChequera(Integer facultadId, Integer lectivoId,
			Integer tipochequeraId) {
		return productotipochequerarepository.findAllByFacultadIdAndLectivoIdAndTipochequeraId(facultadId, lectivoId, tipochequeraId);
	}

	public Producto findByProductoId(Integer productoId) {
		return repository.findByProductoId(productoId).orElseThrow(() -> new ProductoException(productoId));
	}

}
