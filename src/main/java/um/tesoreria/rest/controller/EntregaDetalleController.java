/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.rest.kotlin.model.EntregaDetalle;
import um.tesoreria.rest.repository.EntregaDetalleException;
import um.tesoreria.rest.service.EntregaDetalleService;
import um.tesoreria.rest.repository.EntregaDetalleException;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/entregaDetalle")
public class EntregaDetalleController {

	@Autowired
	private EntregaDetalleService service;

	@GetMapping("/proveedorMovimiento/{proveedorMovimientoId}")
	public ResponseEntity<List<EntregaDetalle>> findAllByProveedorMovimientoId(
			@PathVariable Long proveedorMovimientoId) {
		return new ResponseEntity<List<EntregaDetalle>>(service.findAllByProveedorMovimientoId(proveedorMovimientoId),
				HttpStatus.OK);
	}

	@PostMapping("/proveedorMovimiento")
	public ResponseEntity<List<EntregaDetalle>> findAllByProveedorMovimientoIds(
			@RequestBody List<Long> proveedorMovimientoIds) {
		return new ResponseEntity<List<EntregaDetalle>>(service.findAllByProveedorMovimientoIds(proveedorMovimientoIds),
				HttpStatus.OK);
	}

	@GetMapping("/{entregaDetalleId}")
	public ResponseEntity<EntregaDetalle> findByEntregaDetalleId(@PathVariable Long entregaDetalleId) {
		try {
			return new ResponseEntity<EntregaDetalle>(service.findByEntregaDetalleId(entregaDetalleId), HttpStatus.OK);
		} catch (EntregaDetalleException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
