/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
public class MateriaController {

	@Autowired
	private MateriaService service;

	@GetMapping("/")
	public ResponseEntity<List<Materia>> findAll() {
		return new ResponseEntity<List<Materia>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/facultad/{facultadId}")
	public ResponseEntity<List<Materia>> findAllByFacultadId(@PathVariable Integer facultadId) {
		return new ResponseEntity<List<Materia>>(service.findAllByFacultadId(facultadId), HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{planId}/{materiaId}")
	public ResponseEntity<Materia> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer planId,
			@PathVariable String materiaId) {
		return new ResponseEntity<Materia>(service.findByUnique(facultadId, planId, materiaId), HttpStatus.OK);
	}

}
