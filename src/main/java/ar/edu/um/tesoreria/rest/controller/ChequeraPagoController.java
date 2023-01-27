/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.ChequeraPagoException;
import ar.edu.um.tesoreria.rest.model.ChequeraPago;
import ar.edu.um.tesoreria.rest.service.ChequeraPagoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequerapago")
public class ChequeraPagoController {

	@Autowired
	private ChequeraPagoService service;

	@GetMapping("/{chequeraPagoId}")
	public ResponseEntity<ChequeraPago> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
		try {
			return new ResponseEntity<ChequeraPago>(service.findByChequeraPagoId(chequeraPagoId), HttpStatus.OK);
		} catch (ChequeraPagoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<ChequeraPago> add(@RequestBody ChequeraPago chequeraPago) {
		return new ResponseEntity<ChequeraPago>(service.add(chequeraPago), HttpStatus.OK);
	}

}
