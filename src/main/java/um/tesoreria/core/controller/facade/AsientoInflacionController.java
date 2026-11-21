/**
 * 
 */
package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.AsientoConflictoException;
import um.tesoreria.core.service.facade.AsientoInflacionService;

import java.util.Map;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/asientoinflacion", "/api/tesoreria/core/asientoinflacion"})
@RequiredArgsConstructor
public class AsientoInflacionController {

	private final AsientoInflacionService service;

	@ExceptionHandler(AsientoConflictoException.class)
	public ResponseEntity<Map<String, Object>> asientoConflicto(AsientoConflictoException ex) {
		return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
				"status", 409,
				"error", "Conflict",
				"message", ex.getMessage()));
	}

	@GetMapping("/resultado/{ejercicioId}")
	public ResponseEntity<Void> generateAsientoInflacionResultado(@PathVariable Integer ejercicioId) {
		service.generateAsientoInflacionResultado(ejercicioId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/bienesuso/{ejercicioId}")
	public ResponseEntity<Void> generateAsientoInflacionBienesUso(@PathVariable Integer ejercicioId) {
		service.generateAsientoInflacionBienesUso(ejercicioId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
