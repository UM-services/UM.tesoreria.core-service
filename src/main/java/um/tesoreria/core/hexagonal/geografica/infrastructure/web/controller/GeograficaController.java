/**
 * 
 */
package um.tesoreria.core.hexagonal.geografica.infrastructure.web.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.geografica.application.service.GeograficaService;
import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.GeograficaLectivo;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/geografica", "/api/tesoreria/core/geografica"})
@RequiredArgsConstructor
public class GeograficaController {

	private final GeograficaService service;

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
        return service.findByGeograficaId(geograficaId)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Geografica no encontrada"));
	}

}
