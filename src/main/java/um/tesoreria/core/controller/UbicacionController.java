/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import um.tesoreria.core.kotlin.model.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.UbicacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ubicacion")
public class UbicacionController {

	private final UbicacionService service;

	@Autowired
	public UbicacionController(UbicacionService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Ubicacion>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/sede/{geograficaId}")
	public ResponseEntity<List<Ubicacion>> findAllBySede(@PathVariable Integer geograficaId) {
		if (geograficaId == 0) {
			return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
		}
		return new ResponseEntity<>(service.findAllBySede(geograficaId), HttpStatus.OK);
	}

}
