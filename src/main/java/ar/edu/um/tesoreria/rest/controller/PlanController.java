/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Plan;
import ar.edu.um.tesoreria.rest.service.PlanService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/plan")
public class PlanController {

	@Autowired
	private PlanService service;

	@GetMapping("/")
	public ResponseEntity<List<Plan>> findAll() {
		return new ResponseEntity<List<Plan>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/facultad/{facultadId}")
	public ResponseEntity<List<Plan>> findAllByFacultad(@PathVariable Integer facultadId) {
		return new ResponseEntity<List<Plan>>(service.findAllByFacultadId(facultadId), HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{planId}")
	public ResponseEntity<Plan> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer planId) {
		return new ResponseEntity<Plan>(service.findByFacultadIdAndPlanId(facultadId, planId), HttpStatus.OK);
	}

}
