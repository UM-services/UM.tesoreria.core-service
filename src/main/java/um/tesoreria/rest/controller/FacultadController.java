/**
 * 
 */
package um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.kotlin.model.Facultad;
import um.tesoreria.rest.model.view.FacultadLectivo;
import um.tesoreria.rest.model.view.FacultadLectivoSede;
import um.tesoreria.rest.model.view.FacultadPersona;
import um.tesoreria.rest.service.FacultadService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/facultad")
public class FacultadController {
	@Autowired
	private FacultadService service;

	@GetMapping("/")
	public ResponseEntity<List<Facultad>> findAll() {
		return new ResponseEntity<List<Facultad>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/facultades")
	public ResponseEntity<List<Facultad>> findFacultades() {
		return new ResponseEntity<List<Facultad>>(service.findFacultades(), HttpStatus.OK);
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<FacultadLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
		return new ResponseEntity<List<FacultadLectivo>>(service.findAllByLectivoId(lectivoId), HttpStatus.OK);
	}

	@GetMapping("/bypersona/{personaId}/{documentoId}/{lectivoId}")
	public ResponseEntity<List<FacultadPersona>> findAllByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
		return new ResponseEntity<List<FacultadPersona>>(service.findAllByPersona(personaId, documentoId, lectivoId),
				HttpStatus.OK);
	}

	@GetMapping("/disenho/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<FacultadLectivoSede>> findAllByDisenho(@PathVariable Integer lectivoId,
			@PathVariable Integer geograficaId) {
		return new ResponseEntity<List<FacultadLectivoSede>>(service.findAllByDisenho(lectivoId, geograficaId),
				HttpStatus.OK);
	}

	@GetMapping("/{facultadId}")
	public ResponseEntity<Facultad> findByFacultadId(@PathVariable Integer facultadId) {
		return new ResponseEntity<Facultad>(service.findByFacultadId(facultadId), HttpStatus.OK);
	}
}
