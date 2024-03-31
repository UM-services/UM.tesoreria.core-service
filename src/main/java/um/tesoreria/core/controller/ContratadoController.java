/**
 * 
 */
package um.tesoreria.core.controller;

import um.tesoreria.core.kotlin.model.Contratado;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.ContratadoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratado")
public class ContratadoController {

	@Autowired
	private ContratadoService service;

	@GetMapping("/{contratadoId}")
	public ResponseEntity<Contratado> findByContratadoId(@PathVariable Long contratadoId) {
		return new ResponseEntity<Contratado>(service.findByContratadoId(contratadoId), HttpStatus.OK);
	}

	@GetMapping("/persona/{personaclave}")
	public ResponseEntity<Contratado> findByPersona(@PathVariable Long personaclave) {
		return new ResponseEntity<Contratado>(service.findByPersona(personaclave), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Contratado> add(@RequestBody Contratado contratado) {
		return new ResponseEntity<Contratado>(service.add(contratado), HttpStatus.OK);
	}

	@DeleteMapping("/{contratadoId}")
	public ResponseEntity<Void> delete(@PathVariable Long contratadoId) {
		service.delete(contratadoId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
