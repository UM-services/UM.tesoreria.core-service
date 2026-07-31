/**
 * 
 */
package um.tesoreria.core.controller.view;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.view.ContratadoPersona;
import um.tesoreria.core.service.view.ContratadoPersonaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratadopersona")
@RequiredArgsConstructor
public class ContratadoPersonaController {

	private final ContratadoPersonaService service;

	@GetMapping("/")
	public ResponseEntity<List<ContratadoPersona>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
}
