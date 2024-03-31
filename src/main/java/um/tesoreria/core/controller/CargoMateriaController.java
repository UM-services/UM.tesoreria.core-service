/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.CargoMateria;
import um.tesoreria.core.service.CargoMateriaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cargomateria")
public class CargoMateriaController {

	@Autowired
	private CargoMateriaService service;

	@GetMapping("/")
	public ResponseEntity<List<CargoMateria>> findAll() {
		return new ResponseEntity<List<CargoMateria>>(service.findAll(), HttpStatus.OK);
	}

}
