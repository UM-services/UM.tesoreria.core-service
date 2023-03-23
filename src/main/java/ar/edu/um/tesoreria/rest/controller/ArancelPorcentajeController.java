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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.ArancelPorcentajeException;
import ar.edu.um.tesoreria.rest.kotlin.model.ArancelPorcentaje;
import ar.edu.um.tesoreria.rest.service.ArancelPorcentajeService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/arancelporcentaje")
public class ArancelPorcentajeController {

	@Autowired
	private ArancelPorcentajeService service;

	@GetMapping("/aranceltipo/{aranceltipoId}")
	public ResponseEntity<List<ArancelPorcentaje>> findAllByArancelTipoID(@PathVariable Integer aranceltipoId) {
		return new ResponseEntity<List<ArancelPorcentaje>>(service.findAllByArancelTipoID(aranceltipoId),
				HttpStatus.OK);
	}

	@GetMapping("/unique/{aranceltipoId}/{productoId}")
	public ResponseEntity<ArancelPorcentaje> findByUnique(@PathVariable Integer aranceltipoId,
			@PathVariable Integer productoId) {
		try {
			return new ResponseEntity<ArancelPorcentaje>(service.findByUnique(aranceltipoId, productoId),
					HttpStatus.OK);
		} catch (ArancelPorcentajeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<ArancelPorcentaje> add(@RequestBody ArancelPorcentaje arancelporcentaje) {
		return new ResponseEntity<ArancelPorcentaje>(service.add(arancelporcentaje), HttpStatus.OK);
	}

	@PutMapping("/{arancelporcentajeId}")
	public ResponseEntity<ArancelPorcentaje> update(@RequestBody ArancelPorcentaje arancelporcentaje,
			@PathVariable Long arancelporcentajeId) {
		return new ResponseEntity<ArancelPorcentaje>(service.update(arancelporcentaje, arancelporcentajeId),
				HttpStatus.OK);
	}

}
