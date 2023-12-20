/**
 * 
 */
package um.tesoreria.rest.controller.facade;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.service.MatriculaService;
import um.tesoreria.rest.service.facade.SincronizeService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/sincronize")
public class SincronizeController {

	private final SincronizeService service;

	private final MatriculaService matriculaService;

	@Autowired
	public SincronizeController(SincronizeService service, MatriculaService matriculaService) {
		this.service = service;
		this.matriculaService = matriculaService;
	}

	@GetMapping("/matricula/{lectivoId}/{facultadId}")
	public ResponseEntity<Void> sincronizeMatricula(@PathVariable Integer lectivoId, @PathVariable Integer facultadId)
			throws CloneNotSupportedException {
		service.sincronizeMatricula(lectivoId, facultadId, matriculaService);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/institucional/{lectivoId}/{facultadId}")
	public ResponseEntity<Void> sincronizeInstitucional(@PathVariable Integer lectivoId,
			@PathVariable Integer facultadId) {
		service.sincronizeInstitucional(lectivoId, facultadId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/carreraalumno/{facultadId}/{personaId}/{documentoId}")
	public ResponseEntity<Void> sincronizeCarreraAlumno(@PathVariable Integer facultadId,
			@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		service.sincronizeCarreraAlumno(facultadId, personaId, documentoId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/carrera/{facultadId}")
	public ResponseEntity<Void> sincronizeCarrera(@PathVariable Integer facultadId) {
		service.sincronizeCarrera(facultadId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
