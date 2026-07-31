/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import um.tesoreria.core.kotlin.model.TipoPago;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.TipoPagoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/tipopago")
@RequiredArgsConstructor
public class TipoPagoController {

	private final TipoPagoService service;

	@GetMapping("/")
	public ResponseEntity<List<TipoPago>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
}
