/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import um.tesoreria.rest.kotlin.model.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.service.UbicacionService;

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

	@GetMapping("/sede/{geograficaId}")
	public ResponseEntity<List<Ubicacion>> findAllBySede(@PathVariable Integer geograficaId) {
		if (geograficaId == 0) {
			return new ResponseEntity<List<Ubicacion>>(service.findAll(), HttpStatus.OK);
		}
		return new ResponseEntity<List<Ubicacion>>(service.findAllBySede(geograficaId), HttpStatus.OK);
	}

}
