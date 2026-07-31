/**
 * 
 */
package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.view.OrdenPagoEntregadoException;
import um.tesoreria.core.model.view.OrdenPagoEntregado;
import um.tesoreria.core.service.view.OrdenPagoEntregadoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ordenPagoEntregado")
@RequiredArgsConstructor
public class OrdenPagoEntregadoController {
	
	private final OrdenPagoEntregadoService service;

	@GetMapping("/{ordenPagoId}")
	public ResponseEntity<OrdenPagoEntregado> findByOrdenPagoId(@PathVariable Long ordenPagoId) {
		try {
			return ResponseEntity.ok(service.findByOrdenPagoId(ordenPagoId));
		} catch (OrdenPagoEntregadoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
