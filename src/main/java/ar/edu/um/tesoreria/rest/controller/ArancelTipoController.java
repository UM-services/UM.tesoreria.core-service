/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.ArancelTipo;
import ar.edu.um.tesoreria.rest.model.view.ArancelTipoLectivo;
import ar.edu.um.tesoreria.rest.service.ArancelTipoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/aranceltipo")
public class ArancelTipoController {

	@Autowired
	private ArancelTipoService service;

	@GetMapping("/")
	public ResponseEntity<List<ArancelTipo>> findAll() {
		return new ResponseEntity<List<ArancelTipo>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<ArancelTipoLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
		return new ResponseEntity<List<ArancelTipoLectivo>>(service.findAllByLectivoId(lectivoId), HttpStatus.OK);
	}

	@GetMapping("/{arancelTipoId}")
	public ResponseEntity<ArancelTipo> findByArancelTipoId(@PathVariable Integer arancelTipoId) {
		return new ResponseEntity<ArancelTipo>(service.findByArancelTipoId(arancelTipoId), HttpStatus.OK);
	}

	@GetMapping("/completo/{arancelTipoIdCompleto}")
	public ResponseEntity<ArancelTipo> findByArancelTipoIdCompleto(@PathVariable Integer arancelTipoIdCompleto) {
		return new ResponseEntity<ArancelTipo>(service.findByArancelTipoIdCompleto(arancelTipoIdCompleto),
				HttpStatus.OK);
	}

	@GetMapping("/last")
	public ResponseEntity<ArancelTipo> findLast() {
		return new ResponseEntity<ArancelTipo>(service.findLast(), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<ArancelTipo> add(@RequestBody ArancelTipo arancelTipo) {
		return new ResponseEntity<ArancelTipo>(service.add(arancelTipo), HttpStatus.OK);
	}

	@PutMapping("/{arancelTipoId}")
	public ResponseEntity<ArancelTipo> update(@RequestBody ArancelTipo arancelTipo,
			@PathVariable Integer arancelTipoId) {
		return new ResponseEntity<ArancelTipo>(service.update(arancelTipo, arancelTipoId), HttpStatus.OK);
	}

	@DeleteMapping("/{arancelTipoId}")
	public ResponseEntity<Void> deleteByAranceltipoId(@PathVariable Integer arancelTipoId) {
		service.deleteByArancelTipoId(arancelTipoId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
}
