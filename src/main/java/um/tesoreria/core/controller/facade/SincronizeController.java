/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.facade.SincronizeService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/sincronize", "/api/tesoreria/core/sincronize"})
@RequiredArgsConstructor
public class SincronizeController {

	private final SincronizeService service;

	@GetMapping("/institucional/{lectivoId}/{facultadId}")
	public ResponseEntity<Void> sincronizeInstitucional(@PathVariable Integer lectivoId,
			@PathVariable Integer facultadId) {
		service.sincronizeInstitucional(lectivoId, facultadId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/carreraalumno/{facultadId}/{personaId}/{documentoId}")
	public ResponseEntity<Void> sincronizeCarreraAlumno(@PathVariable Integer facultadId,
			@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		service.sincronizeCarreraAlumno(facultadId, personaId, documentoId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/carrera/{facultadId}")
	public ResponseEntity<Void> sincronizeCarrera(@PathVariable Integer facultadId) {
		service.sincronizeCarrera(facultadId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
