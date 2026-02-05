/**
 * 
 */
package um.tesoreria.core.hexagonal.persona.infrastructure.web.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import um.tesoreria.core.hexagonal.persona.infrastructure.persistence.entity.PersonaEntity;
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

import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.model.dto.DeudaPersonaDto;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/persona", "/api/tesoreria/core/persona"})
@RequiredArgsConstructor
public class PersonaController {

	private final PersonaService service;

	@GetMapping("/santander")
	public ResponseEntity<List<PersonaEntity>> findAllSantander() {
		return ResponseEntity.ok(service.findAllSantander());
	}

	@GetMapping("/inscriptossinchequera/{facultadId}/{lectivoId}/{geograficaId}/{curso}")
	public ResponseEntity<List<PersonaKey>> findAllInscriptosSinChequera(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer curso) {
		return ResponseEntity.ok(service.findAllInscriptosSinChequera(facultadId, lectivoId, geograficaId, curso));
	}

	@GetMapping("/inscriptossinchequeradefault/{facultadId}/{lectivoId}/{geograficaId}/{claseChequeraId}/{curso}")
	public ResponseEntity<List<PersonaKey>> findAllInscriptosSinChequeraDefault(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer claseChequeraId,
			@PathVariable Integer curso) {
		return ResponseEntity.ok(service.findAllInscriptosSinChequeraDefault(facultadId, lectivoId, geograficaId, claseChequeraId, curso));
	}

	@GetMapping("/preinscriptossinchequera/{facultadId}/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<PersonaKey>> findAllPreInscriptosSinChequera(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId) {
		return ResponseEntity.ok(service.findAllPreInscriptosSinChequera(facultadId, lectivoId, geograficaId));
	}

	@GetMapping("/deudoreslectivo/{facultadId}/{lectivoId}/{geograficaId}/{cuotas}")
	public ResponseEntity<List<PersonaKey>> findAllDeudorByLectivoId(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId, @PathVariable Integer cuotas) {
		return ResponseEntity.ok(service.findAllDeudorByLectivoId(facultadId, lectivoId, geograficaId, cuotas));
	}

	@PostMapping("/unifieds")
	public ResponseEntity<List<PersonaKey>> findByUnifieds(@RequestBody List<String> unifieds) {
		return ResponseEntity.ok(service.findByUnifieds(unifieds));
	}

	@PostMapping("/search")
	public ResponseEntity<List<PersonaKey>> findByStrings(@RequestBody List<String> conditions) {
		return ResponseEntity.ok(service.findByStrings(conditions));
	}

	@GetMapping("/unique/{personaId}/{documentoId}")
	public ResponseEntity<PersonaEntity> findByUnique(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		try {
			return ResponseEntity.ok(service.findByUnique(personaId, documentoId));
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/bypersonaId/{personaId}")
	public ResponseEntity<PersonaEntity> findByPersonaId(@PathVariable BigDecimal personaId) {
		try {
			return ResponseEntity.ok(service.findByPersonaId(personaId));
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/{uniqueId}")
	public ResponseEntity<PersonaEntity> findByUniqueId(@PathVariable Long uniqueId) {
		try {
			return ResponseEntity.ok(service.findByUniqueId(uniqueId));
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/deuda/{personaId}/{documentoId}")
	public ResponseEntity<DeudaPersonaDto> deudaByPersona(@PathVariable BigDecimal personaId,
                                                          @PathVariable Integer documentoId) {
		return ResponseEntity.ok(service.deudaByPersona(personaId, documentoId));
	}

	@GetMapping("/deudaextended/{personaId}/{documentoId}")
	public ResponseEntity<DeudaPersonaDto> deudaByPersonaExtended(@PathVariable BigDecimal personaId,
                                                                  @PathVariable Integer documentoId) {
		return ResponseEntity.ok(service.deudaByPersonaExtended(personaId, documentoId));
	}

	@PostMapping("/")
	public ResponseEntity<PersonaEntity> add(@RequestBody PersonaEntity personaEntity) {
		return ResponseEntity.ok(service.add(personaEntity));
	}

	@PutMapping("/{uniqueId}")
	public ResponseEntity<PersonaEntity> update(@RequestBody PersonaEntity personaEntity, @PathVariable Long uniqueId) {
		return ResponseEntity.ok(service.update(personaEntity, uniqueId));
	}

	@GetMapping("/inscripcion/full/{facultadId}/{personaId}/{documentoId}/{lectivoId}")
	public ResponseEntity<InscripcionFullDto> findInscripcionFull(@PathVariable Integer facultadId, @PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
        try {
            return ResponseEntity.ok(service.findInscripcionFull(facultadId, personaId, documentoId, lectivoId));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
	}

}
