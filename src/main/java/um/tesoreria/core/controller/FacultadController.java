/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.Facultad;
import um.tesoreria.core.model.view.FacultadLectivo;
import um.tesoreria.core.model.view.FacultadLectivoSede;
import um.tesoreria.core.model.view.FacultadPersona;
import um.tesoreria.core.service.FacultadService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/facultad", "/api/tesoreria/core/facultad"})
public class FacultadController {

	private final FacultadService service;

	public FacultadController(FacultadService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Facultad>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/facultades")
	public ResponseEntity<List<Facultad>> findFacultades() {
		return new ResponseEntity<>(service.findFacultades(), HttpStatus.OK);
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<FacultadLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
		return new ResponseEntity<>(service.findAllByLectivoId(lectivoId), HttpStatus.OK);
	}

	@GetMapping("/bypersona/{personaId}/{documentoId}/{lectivoId}")
	public ResponseEntity<List<FacultadPersona>> findAllByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
		return new ResponseEntity<>(service.findAllByPersona(personaId, documentoId, lectivoId),
				HttpStatus.OK);
	}

	@GetMapping("/disenho/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<FacultadLectivoSede>> findAllByDisenho(@PathVariable Integer lectivoId,
			@PathVariable Integer geograficaId) {
		return new ResponseEntity<>(service.findAllByDisenho(lectivoId, geograficaId),
				HttpStatus.OK);
	}

	@GetMapping("/{facultadId}")
	public ResponseEntity<Facultad> findByFacultadId(@PathVariable Integer facultadId) {
		return new ResponseEntity<>(service.findByFacultadId(facultadId), HttpStatus.OK);
	}

}
