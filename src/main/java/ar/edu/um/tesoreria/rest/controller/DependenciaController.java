/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.Dependencia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.service.DependenciaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/dependencia")
public class DependenciaController {
	
	@Autowired
	public DependenciaService service;

	@GetMapping("/")
	public ResponseEntity<List<Dependencia>> findAll() {
		return new ResponseEntity<List<Dependencia>>(service.findAll(), HttpStatus.OK);
	}

}
