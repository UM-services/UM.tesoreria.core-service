/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.ProveedorValor;
import ar.edu.um.tesoreria.rest.service.ProveedorValorService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedorvalor")
public class ProveedorValorController {

	@Autowired
	private ProveedorValorService service;

	@GetMapping("/{proveedorValorId}")
	public ResponseEntity<ProveedorValor> findById(@PathVariable Long proveedorValorId) {
		return new ResponseEntity<ProveedorValor>(service.findByProveedorvalorId(proveedorValorId), HttpStatus.OK);
	}

	@GetMapping("/valormovimiento/{valorMovimientoId}")
	public ResponseEntity<ProveedorValor> findByValorMovimientoId(@PathVariable Long valorMovimientoId) {
		return new ResponseEntity<ProveedorValor>(service.findByValorMovimientoId(valorMovimientoId), HttpStatus.OK);
	}

}
