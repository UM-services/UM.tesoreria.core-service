/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
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

import um.tesoreria.core.exception.MatriculaException;
import um.tesoreria.core.model.Matricula;
import um.tesoreria.core.service.MatriculaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/matricula")
@RequiredArgsConstructor
public class MatriculaController {

	private final MatriculaService service;

	@GetMapping("/pendientes")
	public ResponseEntity<List<Matricula>> findPendientes() {
		return new ResponseEntity<List<Matricula>>(service.findPendientes(), HttpStatus.OK);
	}

	@GetMapping("/{matriculaId}")
	public ResponseEntity<Matricula> findByMatriculaId(@PathVariable Long matriculaId) {
		try {
            return ResponseEntity.ok(service.findByMatriculaId(matriculaId));
		} catch (MatriculaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{facultadId}/{personaId}/{documentoId}/{lectivoId}/{clasechequeraId}")
	public ResponseEntity<Matricula> findByUnique(@PathVariable Integer facultadId, @PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer lectivoId, @PathVariable Integer clasechequeraId) {
		try {
            return ResponseEntity.ok(service.findByUnique(facultadId, personaId, documentoId, lectivoId, clasechequeraId));
		} catch (MatriculaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<Matricula> add(@RequestBody Matricula matricula) {
        return ResponseEntity.ok(service.add(matricula));
	}

	@PutMapping("/{matriculaId}")
	public ResponseEntity<Matricula> update(@RequestBody Matricula matricula, @PathVariable Long matriculaId) {
        return ResponseEntity.ok(service.update(matricula, matriculaId));
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
