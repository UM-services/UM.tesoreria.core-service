/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import um.tesoreria.core.kotlin.model.Geografica;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.GeograficaLectivo;
import um.tesoreria.core.service.GeograficaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/geografica")
public class GeograficaController {
	@Autowired
	private GeograficaService service;

	@GetMapping("/")
	public ResponseEntity<List<Geografica>> findAll() {
		return new ResponseEntity<List<Geografica>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/sede/{geograficaId}")
	public ResponseEntity<List<Geografica>> findAllBySede(@PathVariable Integer geograficaId) {
		return new ResponseEntity<List<Geografica>>(service.findAllBySede(geograficaId), HttpStatus.OK);
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<GeograficaLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
		return new ResponseEntity<List<GeograficaLectivo>>(service.findAllByLectivoId(lectivoId), HttpStatus.OK);
	}

	@GetMapping("/{geograficaId}")
	public ResponseEntity<Geografica> findByGeograficaId(@PathVariable Integer geograficaId) {
		return new ResponseEntity<Geografica>(service.findByGeograficaId(geograficaId), HttpStatus.OK);
	}
}
