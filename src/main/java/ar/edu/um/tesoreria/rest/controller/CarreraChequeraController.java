/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.CarreraChequera;
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

import ar.edu.um.tesoreria.rest.exception.CarreraChequeraException;
import ar.edu.um.tesoreria.rest.service.CarreraChequeraService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/carrerachequera")
public class CarreraChequeraController {

	@Autowired
	private CarreraChequeraService service;

	@GetMapping("/curso/{facultadId}/{lectivoId}/{geograficaId}/{claseChequeraId}/{curso}")
	public ResponseEntity<List<CarreraChequera>> findAllByCurso(@PathVariable Integer facultadId,
																@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer claseChequeraId,
																@PathVariable Integer curso) {
		return new ResponseEntity<List<CarreraChequera>>(
				service.findAllByFacultadIdAndLectivoIdAndGeograficaIdAndClaseChequeraIdAndCurso(facultadId, lectivoId,
						geograficaId, claseChequeraId, curso),
				HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{lectivoId}/{planId}/{carreraId}/{claseChequeraId}/{curso}/{geograficaId}")
	public ResponseEntity<CarreraChequera> findByUnique(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer planId, @PathVariable Integer carreraId,
			@PathVariable Integer claseChequeraId, @PathVariable Integer curso, @PathVariable Integer geograficaId) {
		try {
			return new ResponseEntity<CarreraChequera>(service.findByUnique(facultadId, lectivoId, planId, carreraId,
					claseChequeraId, curso, geograficaId), HttpStatus.OK);
		} catch (CarreraChequeraException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<CarreraChequera> add(@RequestBody CarreraChequera carrerachequera) {
		return new ResponseEntity<CarreraChequera>(service.add(carrerachequera), HttpStatus.OK);
	}

	@PutMapping("/{carrerachequeraId}")
	public ResponseEntity<CarreraChequera> update(@RequestBody CarreraChequera carrerachequera,
			@PathVariable Long carrerachequeraId) {
		return new ResponseEntity<CarreraChequera>(service.update(carrerachequera, carrerachequeraId), HttpStatus.OK);
	}

}
