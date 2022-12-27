/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Producto;
import ar.edu.um.tesoreria.rest.model.view.ProductoTipoChequera;
import ar.edu.um.tesoreria.rest.service.ProductoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/producto")
public class ProductoController {
	@Autowired
	private ProductoService service;

	@GetMapping("/")
	public ResponseEntity<List<Producto>> findAll() {
		return new ResponseEntity<List<Producto>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/bytipochequera/{facultadId}/{lectivoId}/{tipochequeraId}")
	public ResponseEntity<List<ProductoTipoChequera>> findAllByTipoChequera(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipochequeraId) {
		return new ResponseEntity<List<ProductoTipoChequera>>(
				service.findAllByTipoChequera(facultadId, lectivoId, tipochequeraId), HttpStatus.OK);
	}

	@GetMapping("/{productoId}")
	public ResponseEntity<Producto> findByProductoId(@PathVariable Integer productoId) {
		return new ResponseEntity<Producto>(service.findByProductoId(productoId), HttpStatus.OK);
	}
}
