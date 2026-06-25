/**
 * 
 */
package um.tesoreria.core.hexagonal.arancelPorcentaje.infrastructure.web.controller;

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

import um.tesoreria.core.hexagonal.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.arancelPorcentaje.infrastructure.persistence.entity.ArancelPorcentajeEntity;
import um.tesoreria.core.hexagonal.arancelPorcentaje.application.service.ArancelPorcentajeService;

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
	public ResponseEntity<List<ArancelPorcentajeEntity>> findAllByArancelTipoID(@PathVariable Integer aranceltipoId) {
		return new ResponseEntity<List<ArancelPorcentajeEntity>>(service.findAllByArancelTipoID(aranceltipoId),
				HttpStatus.OK);
	}

	@GetMapping("/unique/{aranceltipoId}/{productoId}")
	public ResponseEntity<ArancelPorcentajeEntity> findByUnique(@PathVariable Integer aranceltipoId,
	                                                            @PathVariable Integer productoId) {
		try {
			return new ResponseEntity<ArancelPorcentajeEntity>(service.findByUnique(aranceltipoId, productoId),
					HttpStatus.OK);
		} catch (ArancelPorcentajeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<ArancelPorcentajeEntity> add(@RequestBody ArancelPorcentajeEntity arancelporcentaje) {
		return new ResponseEntity<ArancelPorcentajeEntity>(service.add(arancelporcentaje), HttpStatus.OK);
	}

	@PutMapping("/{arancelporcentajeId}")
	public ResponseEntity<ArancelPorcentajeEntity> update(@RequestBody ArancelPorcentajeEntity arancelporcentaje,
	                                                      @PathVariable Long arancelporcentajeId) {
		return new ResponseEntity<ArancelPorcentajeEntity>(service.update(arancelporcentaje, arancelporcentajeId),
				HttpStatus.OK);
	}

}
