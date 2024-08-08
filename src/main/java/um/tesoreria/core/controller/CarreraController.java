/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import um.tesoreria.core.kotlin.model.Carrera;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.CarreraService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/carrera", "/api/tesoreria/core/carrera"})
public class CarreraController {

	private final CarreraService service;

	public CarreraController(CarreraService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Carrera>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{planId}/{carreraId}")
	public ResponseEntity<Carrera> findByFacultadIdAndPlanIdAndCarreraId(@PathVariable Integer facultadId,
			@PathVariable Integer planId, @PathVariable Integer carreraId) {
		return new ResponseEntity<>(service.findByFacultadIdAndPlanIdAndCarreraId(facultadId, planId, carreraId),
				HttpStatus.OK);
	}

}
