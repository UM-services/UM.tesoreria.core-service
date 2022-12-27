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
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.EntregaNotFoundException;
import ar.edu.um.tesoreria.rest.model.Entrega;
import ar.edu.um.tesoreria.rest.service.EntregaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/entrega")
public class EntregaController {

	@Autowired
	private EntregaService service;

	@GetMapping("/detalle/{proveedorMovimientoId}")
	public ResponseEntity<List<Entrega>> findAllDetalleByProveedorMovimientoId(
			@PathVariable Long proveedorMovimientoId) {
		return new ResponseEntity<List<Entrega>>(
				service.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, true), HttpStatus.OK);
	}

	@GetMapping("/{entregaId}")
	public ResponseEntity<Entrega> findByEntregaId(@PathVariable Long entregaId) {
		try {
			return new ResponseEntity<Entrega>(service.findByEntregaId(entregaId), HttpStatus.OK);
		} catch (EntregaNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
