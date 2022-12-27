/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.service.facade.AsientoInflacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/asientoinflacion")
public class AsientoInflacionController {

	@Autowired
	private AsientoInflacionService service;

	@GetMapping("/resultado/{ejercicioId}")
	public ResponseEntity<Void> generateAsientoInflacionResultado(@PathVariable Integer ejercicioId) {
		service.generateAsientoInflacionResultado(ejercicioId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/bienesuso/{ejercicioId}")
	public ResponseEntity<Void> generateAsientoInflacionBienesUso(@PathVariable Integer ejercicioId) {
		service.generateAsientoInflacionBienesUso(ejercicioId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
