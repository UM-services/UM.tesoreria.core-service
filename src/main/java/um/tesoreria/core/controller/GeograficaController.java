/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.GeograficaException;
import um.tesoreria.core.kotlin.model.Geografica;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.GeograficaLectivo;
import um.tesoreria.core.service.GeograficaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/geografica", "/api/tesoreria/core/geografica"})
public class GeograficaController {

	private final GeograficaService service;

    public GeograficaController(GeograficaService service) {
        this.service = service;
    }

	@GetMapping("/")
	public ResponseEntity<List<Geografica>> findAll() {
        return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/sede/{geograficaId}")
	public ResponseEntity<List<Geografica>> findAllBySede(@PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllBySede(geograficaId));
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<GeograficaLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllByLectivoId(lectivoId));
	}

	@GetMapping("/{geograficaId}")
	public ResponseEntity<Geografica> findByGeograficaId(@PathVariable Integer geograficaId) {
        try {
            return ResponseEntity.ok(service.findByGeograficaId(geograficaId));
        } catch (GeograficaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

}
