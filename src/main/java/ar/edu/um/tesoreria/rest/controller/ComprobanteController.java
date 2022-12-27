/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.ComprobanteNotFoundException;
import ar.edu.um.tesoreria.rest.model.Comprobante;
import ar.edu.um.tesoreria.rest.service.ComprobanteService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/comprobante")
public class ComprobanteController {

	@Autowired
	private ComprobanteService service;

	@GetMapping("/")
	public ResponseEntity<List<Comprobante>> findAll() {
		return new ResponseEntity<List<Comprobante>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/{comprobanteId}")
	public ResponseEntity<Comprobante> findByComprobanteId(@PathVariable Integer comprobanteId) {
		try {
			return new ResponseEntity<Comprobante>(service.findByComprobanteId(comprobanteId), HttpStatus.OK);
		} catch (ComprobanteNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
