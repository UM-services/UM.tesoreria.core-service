/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Ubicacion;
import ar.edu.um.tesoreria.rest.service.UbicacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ubicacion")
public class UbicacionController {
	
	@Autowired
	private UbicacionService service;

	@GetMapping("/")
	public ResponseEntity<List<Ubicacion>> findAll() {
		return new ResponseEntity<List<Ubicacion>>(service.findAll(), HttpStatus.OK);
	}
}
