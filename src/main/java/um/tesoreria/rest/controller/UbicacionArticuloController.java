/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.model.UbicacionArticulo;
import um.tesoreria.rest.service.UbicacionArticuloService;
import um.tesoreria.rest.model.UbicacionArticulo;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ubicacionArticulo")
public class UbicacionArticuloController {
	
	@Autowired
	private UbicacionArticuloService service;

	@GetMapping("/")
	public ResponseEntity<List<UbicacionArticulo>> findAll() {
		return new ResponseEntity<List<UbicacionArticulo>>(service.findAll(), HttpStatus.OK);
	}
}
