/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.Entrega;
import jakarta.annotation.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ar.edu.um.tesoreria.rest.exception.EntregaException;
import ar.edu.um.tesoreria.rest.service.EntregaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/entrega")
public class EntregaController {

	@Resource
	private EntregaService service;

	@GetMapping("/detalle/{proveedorMovimientoId}")
	public ResponseEntity<List<Entrega>> findAllDetalleByProveedorMovimientoId(@PathVariable Long proveedorMovimientoId)
			throws JsonProcessingException {
		return new ResponseEntity<List<Entrega>>(
				service.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, true), HttpStatus.OK);
	}

	@PostMapping("/detalle")
	public ResponseEntity<List<Entrega>> findAllDetalleByProveedorMovimientosIds(
			@RequestBody List<Long> proveedorMovimientoIds) throws JsonProcessingException {
		return new ResponseEntity<List<Entrega>>(
				service.findAllDetalleByProveedorMovimientoIds(proveedorMovimientoIds, true), HttpStatus.OK);
	}

	@GetMapping("/{entregaId}")
	public ResponseEntity<Entrega> findByEntregaId(@PathVariable Long entregaId) {
		try {
			return new ResponseEntity<Entrega>(service.findByEntregaId(entregaId), HttpStatus.OK);
		} catch (EntregaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
