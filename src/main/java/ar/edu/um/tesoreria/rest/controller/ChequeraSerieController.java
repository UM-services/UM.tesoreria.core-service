/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.um.tesoreria.rest.kotlin.model.ChequeraSerie;
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

import ar.edu.um.tesoreria.rest.model.view.ChequeraAlta;
import ar.edu.um.tesoreria.rest.model.view.ChequeraIncompleta;
import ar.edu.um.tesoreria.rest.model.view.ChequeraKey;
import ar.edu.um.tesoreria.rest.service.ChequeraSerieService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequeraserie")
public class ChequeraSerieController {

	@Autowired
	private ChequeraSerieService service;

	@GetMapping("/persona/{personaId}/{documentoId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByPersona(@PathVariable BigDecimal personaId,
																@PathVariable Integer documentoId) {
		return new ResponseEntity<List<ChequeraSerie>>(service.findAllByPersona(personaId, documentoId), HttpStatus.OK);
	}

	@GetMapping("/personaextended/{personaId}/{documentoId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByPersonaExtended(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<List<ChequeraSerie>>(service.findAllByPersonaExtended(personaId, documentoId),
				HttpStatus.OK);
	}

	@GetMapping("/facultad/{personaId}/{documentoId}/{facultadId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByFacultad(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer facultadId) {
		return new ResponseEntity<List<ChequeraSerie>>(service.findAllByFacultad(personaId, documentoId, facultadId),
				HttpStatus.OK);
	}

	@GetMapping("/facultadextended/{personaId}/{documentoId}/{facultadId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByFacultadExtended(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer facultadId) {
		return new ResponseEntity<List<ChequeraSerie>>(
				service.findAllByFacultadExtended(personaId, documentoId, facultadId), HttpStatus.OK);
	}

	@GetMapping("/personaLectivo/{personaId}/{documentoId}/{lectivoId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByPersonaLectivo(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
		return new ResponseEntity<List<ChequeraSerie>>(service.findAllByPersonaLectivo(personaId, documentoId, lectivoId), HttpStatus.OK);
	}

	@GetMapping("/incompletas/{lectivoId}/{facultadId}/{geograficaId}")
	public ResponseEntity<List<ChequeraIncompleta>> findAllIncompletas(@PathVariable Integer lectivoId,
			@PathVariable Integer facultadId, @PathVariable Integer geograficaId) {
		return new ResponseEntity<List<ChequeraIncompleta>>(
				service.findAllIncompletas(lectivoId, facultadId, geograficaId), HttpStatus.OK);
	}

	@GetMapping("/altas/{lectivoId}/{facultadId}/{geograficaId}")
	public ResponseEntity<List<ChequeraAlta>> findAllAltas(@PathVariable Integer lectivoId,
			@PathVariable Integer facultadId, @PathVariable Integer geograficaId) {
		return new ResponseEntity<List<ChequeraAlta>>(service.findAllAltas(lectivoId, facultadId, geograficaId),
				HttpStatus.OK);
	}

	@GetMapping("/bynumber/{facultadId}/{chequeraserieId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByNumber(@PathVariable Integer facultadId,
			@PathVariable Long chequeraserieId) {
		return new ResponseEntity<List<ChequeraSerie>>(service.findAllByNumber(facultadId, chequeraserieId),
				HttpStatus.OK);
	}

	@PostMapping("/documentos/{facultadId}/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<ChequeraSerie>> findAllByDocumentos(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId,
			@RequestBody List<BigDecimal> personaIds) {
		return new ResponseEntity<List<ChequeraSerie>>(
				service.findAllByDocumentos(facultadId, lectivoId, geograficaId, personaIds), HttpStatus.OK);
	}

	@GetMapping("/cbu/{cbu}/{debitoTipoId}")
	public ResponseEntity<List<ChequeraKey>> findAllByCbu(@PathVariable String cbu,
			@PathVariable Integer debitoTipoId) {
		return new ResponseEntity<List<ChequeraKey>>(service.findAllByCbu(cbu, debitoTipoId), HttpStatus.OK);
	}

	@GetMapping("/visa/{numeroTarjeta}/{debitoTipoId}")
	public ResponseEntity<List<ChequeraKey>> findAllByVisa(@PathVariable String numeroTarjeta,
			@PathVariable Integer debitoTipoId) {
		return new ResponseEntity<List<ChequeraKey>>(service.findAllByVisa(numeroTarjeta, debitoTipoId), HttpStatus.OK);
	}

	@GetMapping("/{chequeraId}")
	public ResponseEntity<ChequeraSerie> findByChequeraId(@PathVariable Long chequeraId) {
		return new ResponseEntity<ChequeraSerie>(service.findByChequeraId(chequeraId), HttpStatus.OK);
	}

	@GetMapping("/extended/{chequeraId}")
	public ResponseEntity<ChequeraSerie> findByChequeraIdExtended(@PathVariable Long chequeraId) {
		return new ResponseEntity<ChequeraSerie>(service.findByChequeraIdExtended(chequeraId), HttpStatus.OK);
	}

	@GetMapping("/unique/{facultadId}/{tipochequeraId}/{chequeraserieId}")
	public ResponseEntity<ChequeraSerie> findByUnique(@PathVariable Integer facultadId,
			@PathVariable Integer tipochequeraId, @PathVariable Long chequeraserieId) {
		return new ResponseEntity<ChequeraSerie>(service.findByUnique(facultadId, tipochequeraId, chequeraserieId),
				HttpStatus.OK);
	}

	@GetMapping("/uniqueextended/{facultadId}/{tipochequeraId}/{chequeraserieId}")
	public ResponseEntity<ChequeraSerie> findByUniqueExtended(@PathVariable Integer facultadId,
			@PathVariable Integer tipochequeraId, @PathVariable Long chequeraserieId) {
		return new ResponseEntity<ChequeraSerie>(
				service.findByUniqueExtended(facultadId, tipochequeraId, chequeraserieId), HttpStatus.OK);
	}

	@GetMapping("/setpaypertic/{facultadId}/{tipochequeraId}/{chequeraserieId}/{flag}")
	public ResponseEntity<ChequeraSerie> setPayPerTic(@PathVariable Integer facultadId,
			@PathVariable Integer tipochequeraId, @PathVariable Long chequeraserieId, @PathVariable Byte flag) {
		return new ResponseEntity<ChequeraSerie>(
				service.setPayPerTic(facultadId, tipochequeraId, chequeraserieId, flag), HttpStatus.OK);
	}

	@PutMapping("/{chequeraId}")
	public ResponseEntity<ChequeraSerie> update(@RequestBody ChequeraSerie chequeraserie,
			@PathVariable Long chequeraId) {
		return new ResponseEntity<ChequeraSerie>(service.update(chequeraserie, chequeraId), HttpStatus.OK);
	}
}
