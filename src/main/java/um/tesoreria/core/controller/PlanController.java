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

import um.tesoreria.core.kotlin.model.Plan;
import um.tesoreria.core.service.PlanService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/plan")
public class PlanController {

	private final PlanService service;

	public PlanController(PlanService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Plan>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/facultad/{facultadId}")
	public ResponseEntity<List<Plan>> findAllByFacultad(@PathVariable Integer facultadId) {
		return new ResponseEntity<>(service.findAllByFacultadId(facultadId), HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{planId}")
	public ResponseEntity<Plan> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer planId) {
		return new ResponseEntity<>(service.findByFacultadIdAndPlanId(facultadId, planId), HttpStatus.OK);
	}

}
