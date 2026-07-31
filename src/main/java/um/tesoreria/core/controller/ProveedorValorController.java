/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.ProveedorValor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.ProveedorValorService;

import java.util.List;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedorValor")
@RequiredArgsConstructor
public class ProveedorValorController {

	private final ProveedorValorService service;

	@GetMapping("/proveedorMovimiento/{proveedorMovimientoId}")
	public ResponseEntity<List<ProveedorValor>> findAllByProveedorMovimientoId(@PathVariable Long proveedorMovimientoId) {
		return ResponseEntity.ok(service.findAllByProveedorMovimientoId(proveedorMovimientoId));
	}

	@GetMapping("/{proveedorValorId}")
	public ResponseEntity<ProveedorValor> findByProveedorValorId(@PathVariable Long proveedorValorId) {
		return ResponseEntity.ok(service.findByProveedorValorId(proveedorValorId));
	}

	@GetMapping("/valorMovimiento/{valorMovimientoId}")
	public ResponseEntity<ProveedorValor> findByValorMovimientoId(@PathVariable Long valorMovimientoId) {
		return ResponseEntity.ok(service.findByValorMovimientoId(valorMovimientoId));
	}

}
