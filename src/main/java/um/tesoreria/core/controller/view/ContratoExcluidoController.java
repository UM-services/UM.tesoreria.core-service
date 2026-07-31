/**
 * 
 */
package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.ContratoExcluido;
import um.tesoreria.core.service.view.ContratoExcluidoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratoexcluido")
@RequiredArgsConstructor
public class ContratoExcluidoController {

	private final ContratoExcluidoService service;

	@GetMapping("/")
	public ResponseEntity<List<ContratoExcluido>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
}
