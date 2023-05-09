/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import ar.edu.um.tesoreria.rest.kotlin.model.ProveedorValor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.service.ProveedorValorService;

import java.util.List;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedorValor")
public class ProveedorValorController {

	@Autowired
	private ProveedorValorService service;

	@GetMapping("/proveedorMovimiento/{proveedorMovimientoId}")
	public ResponseEntity<List<ProveedorValor>> findAllByProveedorMovimientoId(@PathVariable Long proveedorMovimientoId) {
		return new ResponseEntity<List<ProveedorValor>>(service.findAllByProveedorMovimientoId(proveedorMovimientoId), HttpStatus.OK);
	}

	@GetMapping("/{proveedorValorId}")
	public ResponseEntity<ProveedorValor> findByProveedorValorId(@PathVariable Long proveedorValorId) {
		return new ResponseEntity<ProveedorValor>(service.findByProveedorValorId(proveedorValorId), HttpStatus.OK);
	}

	@GetMapping("/valorMovimiento/{valorMovimientoId}")
	public ResponseEntity<ProveedorValor> findByValorMovimientoId(@PathVariable Long valorMovimientoId) {
		return new ResponseEntity<ProveedorValor>(service.findByValorMovimientoId(valorMovimientoId), HttpStatus.OK);
	}

}
