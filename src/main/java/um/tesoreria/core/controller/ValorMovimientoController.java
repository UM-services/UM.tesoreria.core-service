/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.ValorMovimientoService;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/valorMovimiento")
@RequiredArgsConstructor
public class ValorMovimientoController {

	private final ValorMovimientoService service;

	@GetMapping("/{valorMovimientoId}")
	public ResponseEntity<ValorMovimiento> findByValorMovimientoId(@PathVariable Long valorMovimientoId) {
		try {
			return ResponseEntity.ok(service.findByValorMovimientoId(valorMovimientoId));
		} catch (ValorMovimientoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/numero/{valorId}/{numero}")
	public ResponseEntity<ValorMovimiento> findByNumero(@PathVariable Integer valorId, @PathVariable Long numero) {
		try {
			return ResponseEntity.ok(service.findByNumero(valorId, numero));
		} catch (ValorMovimientoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/banco/{valorId}/{numero}/{bancariaId}")
	public ResponseEntity<ValorMovimiento> findByBanco(@PathVariable Integer valorId, @PathVariable Long numero, @PathVariable Long bancariaId) {
		try {
			return ResponseEntity.ok(service.findByBanco(valorId, numero, bancariaId));
		} catch (ValorMovimientoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
