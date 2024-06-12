/**
 * 
 */
package um.tesoreria.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.Setup;
import um.tesoreria.core.service.SetupService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/setup")
public class SetupController {

	private final SetupService service;

	@Autowired
	public SetupController(SetupService service) {
		this.service = service;
	}

	@GetMapping("/last")
	public ResponseEntity<Setup> findLast() {
		return new ResponseEntity<>(service.findLast(), HttpStatus.OK);
	}

}
