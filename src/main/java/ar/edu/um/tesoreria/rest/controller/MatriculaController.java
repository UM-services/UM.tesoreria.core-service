/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.MatriculaException;
import ar.edu.um.tesoreria.rest.model.Matricula;
import ar.edu.um.tesoreria.rest.service.MatriculaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/matricula")
public class MatriculaController {

	@Autowired
	private MatriculaService service;

	@GetMapping("/pendientes")
	public ResponseEntity<List<Matricula>> findPendientes() {
		return new ResponseEntity<List<Matricula>>(service.findPendientes(), HttpStatus.OK);
	}

	@GetMapping("/{matriculaId}")
	public ResponseEntity<Matricula> findByMatriculaId(@PathVariable Long matriculaId) {
		try {
			return new ResponseEntity<Matricula>(service.findByMatriculaId(matriculaId), HttpStatus.OK);
		} catch (MatriculaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{facultadId}/{personaId}/{documentoId}/{lectivoId}/{clasechequeraId}")
	public ResponseEntity<Matricula> findByUnique(@PathVariable Integer facultadId, @PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer lectivoId, @PathVariable Integer clasechequeraId) {
		try {
			return new ResponseEntity<Matricula>(
					service.findByUnique(facultadId, personaId, documentoId, lectivoId, clasechequeraId),
					HttpStatus.OK);
		} catch (MatriculaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<Matricula> add(@RequestBody Matricula matricula) {
		return new ResponseEntity<Matricula>(service.add(matricula), HttpStatus.OK);
	}

	@PutMapping("/{matriculaId}")
	public ResponseEntity<Matricula> update(@RequestBody Matricula matricula, @PathVariable Long matriculaId) {
		return new ResponseEntity<Matricula>(service.update(matricula, matriculaId), HttpStatus.OK);
	}

	@GetMapping("/depurate")
	@Scheduled(cron = "0 0 4 * * *")
	private void updateAndDepurate() throws CloneNotSupportedException {
		service.updateAndDepurate();
	}

	@GetMapping("/depurate_test")
	private void depurate() {
		service.depurate(32);
	}
}
