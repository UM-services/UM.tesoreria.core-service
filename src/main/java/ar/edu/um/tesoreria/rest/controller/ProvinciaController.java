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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Provincia;
import ar.edu.um.tesoreria.rest.service.ProvinciaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/provincia")
public class ProvinciaController {
	@Autowired
	private ProvinciaService service;

	@GetMapping("/facultad/{facultadId}")
	public ResponseEntity<List<Provincia>> findAllByFacultadId(@PathVariable Integer facultadId) {
		return new ResponseEntity<List<Provincia>>(service.findAllByFacultadId(facultadId), HttpStatus.OK);
	}

	@GetMapping("/{uniqueId}")
	public ResponseEntity<Provincia> findByUnicoId(@PathVariable Long uniqueId) {
		return new ResponseEntity<Provincia>(service.findByUniqueId(uniqueId), HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{provinciaId}")
	public ResponseEntity<Provincia> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer provinciaId) {
		return new ResponseEntity<Provincia>(service.findByUnique(facultadId, provinciaId), HttpStatus.OK);
	}

	@GetMapping("/nombre/{facultadId}")
	public ResponseEntity<Provincia> findByNombre(@PathVariable Integer facultadId,
			@RequestParam("nombre") String nombre) {
		return new ResponseEntity<Provincia>(service.findByNombre(facultadId, nombre), HttpStatus.OK);
	}

	@GetMapping("/last/{facultadId}")
	public ResponseEntity<Provincia> findLastByFacultadId(@PathVariable Integer facultadId) {
		return new ResponseEntity<Provincia>(service.findLastByFacultadId(facultadId), HttpStatus.OK);
	}

}
