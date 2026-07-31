/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.kotlin.model.EntregaDetalle;
import um.tesoreria.core.exception.EntregaDetalleException;
import um.tesoreria.core.service.EntregaDetalleService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/entregaDetalle")
@RequiredArgsConstructor
public class EntregaDetalleController {

	private final EntregaDetalleService service;

	@GetMapping("/proveedorMovimiento/{proveedorMovimientoId}")
	public ResponseEntity<List<EntregaDetalle>> findAllByProveedorMovimientoId(
			@PathVariable Long proveedorMovimientoId) {
		return ResponseEntity.ok(service.findAllByProveedorMovimientoId(proveedorMovimientoId));
	}

	@PostMapping("/proveedorMovimiento")
	public ResponseEntity<List<EntregaDetalle>> findAllByProveedorMovimientoIds(
			@RequestBody List<Long> proveedorMovimientoIds) {
		return ResponseEntity.ok(service.findAllByProveedorMovimientoIds(proveedorMovimientoIds));
	}

	@GetMapping("/{entregaDetalleId}")
	public ResponseEntity<EntregaDetalle> findByEntregaDetalleId(@PathVariable Long entregaDetalleId) {
		try {
			return ResponseEntity.ok(service.findByEntregaDetalleId(entregaDetalleId));
		} catch (EntregaDetalleException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
