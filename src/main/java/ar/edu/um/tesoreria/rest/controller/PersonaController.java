/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
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

import ar.edu.um.tesoreria.rest.exception.PersonaException;
import ar.edu.um.tesoreria.rest.model.Persona;
import ar.edu.um.tesoreria.rest.model.dto.DeudaPersona;
import ar.edu.um.tesoreria.rest.model.view.PersonaKey;
import ar.edu.um.tesoreria.rest.service.PersonaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/persona")
public class PersonaController {

	@Autowired
	private PersonaService service;

	@GetMapping("/santander")
	public ResponseEntity<List<Persona>> findAllSantander() {
		return new ResponseEntity<List<Persona>>(service.findAllSantander(), HttpStatus.OK);
	}

	@GetMapping("/inscriptossinchequera/{facultadId}/{lectivoId}/{geograficaId}/{curso}")
	public ResponseEntity<List<PersonaKey>> findAllInscriptosSinChequera(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer curso) {
		return new ResponseEntity<List<PersonaKey>>(
				service.findAllInscriptosSinChequera(facultadId, lectivoId, geograficaId, curso), HttpStatus.OK);
	}

	@GetMapping("/inscriptossinchequeradefault/{facultadId}/{lectivoId}/{geograficaId}/{claseChequeraId}/{curso}")
	public ResponseEntity<List<PersonaKey>> findAllInscriptosSinChequeraDefault(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer claseChequeraId,
			@PathVariable Integer curso) {
		return new ResponseEntity<List<PersonaKey>>(service.findAllInscriptosSinChequeraDefault(facultadId, lectivoId,
				geograficaId, claseChequeraId, curso), HttpStatus.OK);
	}

	@GetMapping("/preinscriptossinchequera/{facultadId}/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<PersonaKey>> findAllPreInscriptosSinChequera(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId) {
		return new ResponseEntity<List<PersonaKey>>(
				service.findAllPreInscriptosSinChequera(facultadId, lectivoId, geograficaId), HttpStatus.OK);
	}

	@GetMapping("/deudoreslectivo/{facultadId}/{lectivoId}/{geograficaId}/{cuotas}")
	public ResponseEntity<List<PersonaKey>> findAllDeudorByLectivoId(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer cuotas) {
		return new ResponseEntity<List<PersonaKey>>(
				service.findAllDeudorByLectivoId(facultadId, lectivoId, geograficaId, cuotas), HttpStatus.OK);
	}

	@PostMapping("/unifieds")
	public ResponseEntity<List<PersonaKey>> findByUnifieds(@RequestBody List<String> unifieds) {
		return new ResponseEntity<List<PersonaKey>>(service.findByUnifieds(unifieds), HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<PersonaKey>> findByStrings(@RequestBody List<String> conditions) {
		return new ResponseEntity<List<PersonaKey>>(service.findByStrings(conditions), HttpStatus.OK);
	}

	@GetMapping("/unique/{personaId}/{documentoId}")
	public ResponseEntity<Persona> findByUnique(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		try {
			return new ResponseEntity<Persona>(service.findByUnique(personaId, documentoId), HttpStatus.OK);
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/bypersonaId/{personaId}")
	public ResponseEntity<Persona> findByPersonaId(@PathVariable BigDecimal personaId) {
		try {
			return new ResponseEntity<Persona>(service.findByPersonaId(personaId), HttpStatus.OK);
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/{uniqueId}")
	public ResponseEntity<Persona> findByUniqueId(@PathVariable Long uniqueId) {
		try {
			return new ResponseEntity<Persona>(service.findByUniqueId(uniqueId), HttpStatus.OK);
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/deuda/{personaId}/{documentoId}")
	public ResponseEntity<DeudaPersona> deudaByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<DeudaPersona>(service.deudaByPersona(personaId, documentoId), HttpStatus.OK);
	}

	@GetMapping("/deudaextended/{personaId}/{documentoId}")
	public ResponseEntity<DeudaPersona> deudaByPersonaExtended(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<DeudaPersona>(service.deudaByPersonaExtended(personaId, documentoId), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Persona> add(@RequestBody Persona persona) {
		return new ResponseEntity<Persona>(service.add(persona), HttpStatus.OK);
	}

	@PutMapping("/{uniqueId}")
	public ResponseEntity<Persona> update(@RequestBody Persona persona, @PathVariable Long uniqueId) {
		return new ResponseEntity<Persona>(service.update(persona, uniqueId), HttpStatus.OK);
	}

}
