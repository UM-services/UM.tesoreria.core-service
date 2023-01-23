/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import ar.edu.um.tesoreria.rest.exception.ProveedorMovimientoNotFoundException;
import ar.edu.um.tesoreria.rest.model.ProveedorMovimiento;
import ar.edu.um.tesoreria.rest.service.ProveedorMovimientoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedormovimiento")
public class ProveedorMovimientoController {

	@Autowired
	private ProveedorMovimientoService service;

	@GetMapping("/eliminables/{ejercicioId}")
	public ResponseEntity<List<ProveedorMovimiento>> findAllEliminables(@PathVariable Integer ejercicioId) {
		return new ResponseEntity<List<ProveedorMovimiento>>(service.findAllEliminables(ejercicioId), HttpStatus.OK);
	}

	@GetMapping("/asignables/{proveedorId}/{desde}/{hasta}/{geograficaId}/{todos}")
	public ResponseEntity<List<ProveedorMovimiento>> findAllAsignables(@PathVariable Integer proveedorId,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
			@PathVariable Integer geograficaId, @PathVariable Boolean todos) throws JsonProcessingException {
		return new ResponseEntity<List<ProveedorMovimiento>>(
				service.findAllAsignables(proveedorId, desde, hasta, geograficaId, todos), HttpStatus.OK);
	}

	@GetMapping("/{proveedorMovimientoId}")
	public ResponseEntity<ProveedorMovimiento> findByProveedorMovimientoId(@PathVariable Long proveedorMovimientoId) {
		try {
			return new ResponseEntity<ProveedorMovimiento>(service.findByProveedorMovimientoId(proveedorMovimientoId),
					HttpStatus.OK);
		} catch (ProveedorMovimientoNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
