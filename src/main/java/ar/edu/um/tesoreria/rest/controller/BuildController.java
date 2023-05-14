/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import ar.edu.um.tesoreria.rest.kotlin.model.Build;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.service.BuildService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/build")
public class BuildController {

	@Autowired
	private BuildService service;

	@GetMapping("/last")
	public ResponseEntity<Build> findLast() {
		return new ResponseEntity<Build>(service.findLast(), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Build> add() {
		return new ResponseEntity<Build>(service.add(new Build()), HttpStatus.OK);
	}

}
