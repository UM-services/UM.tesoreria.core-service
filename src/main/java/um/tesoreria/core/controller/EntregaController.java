/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import um.tesoreria.core.kotlin.model.Entrega;
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

import um.tesoreria.core.exception.EntregaException;
import um.tesoreria.core.service.EntregaService;

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
		return ResponseEntity.ok(
				service.findAllDetalleByProveedorMovimientoId(proveedorMovimientoId, true));
	}

	@PostMapping("/detalle")
	public ResponseEntity<List<Entrega>> findAllDetalleByProveedorMovimientosIds(
			@RequestBody List<Long> proveedorMovimientoIds) throws JsonProcessingException {
		return ResponseEntity.ok(
				service.findAllDetalleByProveedorMovimientoIds(proveedorMovimientoIds, true));
	}

	@GetMapping("/{entregaId}")
	public ResponseEntity<Entrega> findByEntregaId(@PathVariable Long entregaId) {
		try {
			return ResponseEntity.ok(service.findByEntregaId(entregaId));
		} catch (EntregaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
