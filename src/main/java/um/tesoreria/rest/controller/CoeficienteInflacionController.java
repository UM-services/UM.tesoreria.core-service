/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.model.CoeficienteInflacion;
import um.tesoreria.rest.service.CoeficienteInflacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/coeficienteinflacion")
public class CoeficienteInflacionController {

	@Autowired
	private CoeficienteInflacionService service;

	@GetMapping("/")
	public ResponseEntity<List<CoeficienteInflacion>> findAll() {
		return new ResponseEntity<List<CoeficienteInflacion>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/unique/{anho}/{mes}")
	public ResponseEntity<CoeficienteInflacion> findByUnique(@PathVariable Integer anho, @PathVariable Integer mes) {
		return new ResponseEntity<CoeficienteInflacion>(service.findByUnique(anho, mes), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<CoeficienteInflacion> add(@RequestBody CoeficienteInflacion coeficienteinflacion) {
		return new ResponseEntity<CoeficienteInflacion>(service.add(coeficienteinflacion), HttpStatus.OK);
	}

	@PutMapping("/{coeficienteinflacionId}")
	public ResponseEntity<CoeficienteInflacion> update(@RequestBody CoeficienteInflacion coeficienteinflacion,
			@PathVariable Long coeficienteinflacionId) {
		return new ResponseEntity<CoeficienteInflacion>(service.update(coeficienteinflacion, coeficienteinflacionId),
				HttpStatus.OK);
	}

}
