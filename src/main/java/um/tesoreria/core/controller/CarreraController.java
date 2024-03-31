/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

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
@RequestMapping("/carrera")
public class CarreraController {

	@Autowired
	private CarreraService service;

	@GetMapping("/")
	public ResponseEntity<List<Carrera>> findAll() {
		return new ResponseEntity<List<Carrera>>(service.findAll(), HttpStatus.OK);
	}

}
