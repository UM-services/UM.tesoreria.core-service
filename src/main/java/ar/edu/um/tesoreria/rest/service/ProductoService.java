/**
 * 
 */
package ar.edu.um.tesoreria.rest.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.um.tesoreria.rest.exception.ProductoException;
import ar.edu.um.tesoreria.rest.model.Producto;
import ar.edu.um.tesoreria.rest.model.view.ProductoTipoChequera;
import ar.edu.um.tesoreria.rest.repository.IProductoRepository;
import ar.edu.um.tesoreria.rest.repository.view.IProductoTipoChequeraRepository;

/**
 * @author daniel
 *
 */
@Service
public class ProductoService {
	@Autowired
	private IProductoRepository repository;
	
	@Autowired
	private IProductoTipoChequeraRepository productotipochequerarepository;

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
