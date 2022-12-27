/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Setup;
import ar.edu.um.tesoreria.rest.service.SetupService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/setup")
public class SetupController {

	@Autowired
	private SetupService service;

	@GetMapping("/last")
	public ResponseEntity<Setup> findLast() {
		return new ResponseEntity<Setup>(service.findLast(), HttpStatus.OK);
	}

}
