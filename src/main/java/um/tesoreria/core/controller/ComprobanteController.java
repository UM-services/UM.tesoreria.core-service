/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import um.tesoreria.core.kotlin.model.Comprobante;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.ComprobanteException;
import um.tesoreria.core.service.ComprobanteService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/comprobante", "/api/tesoreria/core/comprobante"})
public class ComprobanteController {

	private final ComprobanteService service;

	public ComprobanteController(ComprobanteService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Comprobante>> findAll() {
		return new ResponseEntity<List<Comprobante>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/{comprobanteId}")
	public ResponseEntity<Comprobante> findByComprobanteId(@PathVariable Integer comprobanteId) {
		try {
			return new ResponseEntity<Comprobante>(service.findByComprobanteId(comprobanteId), HttpStatus.OK);
		} catch (ComprobanteException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
}
