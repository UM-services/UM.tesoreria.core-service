/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.view;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.view.TipoChequeraSede;
import ar.edu.um.tesoreria.rest.service.view.TipoChequeraSedeService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/tipochequerasede")
public class TipoChequeraSedeController {

	@Autowired
	private TipoChequeraSedeService service;

	@GetMapping("/")
	public ResponseEntity<List<TipoChequeraSede>> findAll() {
		return new ResponseEntity<List<TipoChequeraSede>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/sede/{geograficaId}/{modulo}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByGeograficaId(@PathVariable Integer geograficaId,
			@PathVariable String modulo) {
		return new ResponseEntity<List<TipoChequeraSede>>(service.findAllByGeograficaId(geograficaId, modulo),
				HttpStatus.OK);
	}

	@GetMapping("/lectivo/{lectivoId}/{facultadId}/{geograficaId}/{modulo}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByGeograficaId(@PathVariable Integer lectivoId,
			@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable String modulo) {
		return new ResponseEntity<List<TipoChequeraSede>>(
				service.findAllByLectivoIdAndFacultadId(lectivoId, facultadId, geograficaId, modulo), HttpStatus.OK);
	}

	@GetMapping("/persona/{personaId}/{documentoId}/{lectivoId}/{facultadId}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByPersonaId(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer lectivoId, @PathVariable Integer facultadId) {
		return new ResponseEntity<List<TipoChequeraSede>>(
				service.findAllByPersonaId(personaId, documentoId, lectivoId, facultadId), HttpStatus.OK);
	}

	@GetMapping("/tablero/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByLectivoIdAndGeograficaId(@PathVariable Integer lectivoId,
			@PathVariable Integer geograficaId) {
		return new ResponseEntity<List<TipoChequeraSede>>(
				service.findAllByLectivoIdAndGeograficaId(lectivoId, geograficaId), HttpStatus.OK);
	}

	@GetMapping("/{tipoChequeraId}")
	public ResponseEntity<TipoChequeraSede> findByTipoChequeraId(@PathVariable Integer tipoChequeraId) {
		return new ResponseEntity<TipoChequeraSede>(service.findByTipoChequeraId(tipoChequeraId), HttpStatus.OK);
	}
}
