/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.Materia;
import um.tesoreria.core.service.MateriaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/materia")
@RequiredArgsConstructor
public class MateriaController {

	private final MateriaService service;

	@GetMapping("/")
	public ResponseEntity<List<Materia>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/facultad/{facultadId}")
	public ResponseEntity<List<Materia>> findAllByFacultadId(@PathVariable Integer facultadId) {
		return ResponseEntity.ok(service.findAllByFacultadId(facultadId));
	}

	@GetMapping("/unique/{facultadId}/{planId}/{materiaId}")
	public ResponseEntity<Materia> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer planId,
			@PathVariable String materiaId) {
		return ResponseEntity.ok(service.findByUnique(facultadId, planId, materiaId));
	}

}
