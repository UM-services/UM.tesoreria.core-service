/**
 * 
 */
package um.tesoreria.core.controller;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ContratadoException;
import um.tesoreria.core.kotlin.model.Contratado;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.ContratadoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratado")
public class ContratadoController {

	private final ContratadoService service;

    public ContratadoController(ContratadoService service) {
        this.service = service;
    }

	@GetMapping("/{contratadoId}")
	public ResponseEntity<Contratado> findByContratadoId(@PathVariable Long contratadoId) {
        try {
            return ResponseEntity.ok(service.findByContratadoId(contratadoId));
        } catch (ContratadoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
	}

	@GetMapping("/persona/{personaclave}")
	public ResponseEntity<Contratado> findByPersona(@PathVariable Long personaclave) {
        try {
            return ResponseEntity.ok(service.findByPersona(personaclave));
        } catch (ContratadoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
	}

	@PostMapping("/")
	public ResponseEntity<Contratado> add(@RequestBody Contratado contratado) {
        return ResponseEntity.ok(service.add(contratado));
	}

	@DeleteMapping("/{contratadoId}")
	public ResponseEntity<Void> delete(@PathVariable Long contratadoId) {
		service.delete(contratadoId);
        return ResponseEntity.noContent().build();
	}

}
