package um.tesoreria.core.hexagonal.persona.infrastructure.web.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
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
import um.tesoreria.core.hexagonal.persona.application.service.PersonaService;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.DeudaExamenResponse;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.PersonaRequest;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.PersonaResponse;
import um.tesoreria.core.hexagonal.persona.infrastructure.web.mapper.PersonaDtoMapper;
import um.tesoreria.core.model.dto.DeudaPersonaDto;
import um.tesoreria.core.model.view.PersonaKey;

@RestController
@RequestMapping({"/persona", "/api/tesoreria/core/persona"})
@RequiredArgsConstructor
public class PersonaController {

	private final PersonaService service;
	private final PersonaDtoMapper dtoMapper;

	@GetMapping("/santander")
	public ResponseEntity<List<PersonaResponse>> findAllSantander() {
		List<PersonaResponse> responses = service.findAllSantander().stream()
				.map(dtoMapper::toResponse)
				.collect(Collectors.toList());
		return ResponseEntity.ok(responses);
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
	public ResponseEntity<PersonaResponse> findByUnique(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		try {
			Persona domain = service.findByUnique(personaId, documentoId);
			return ResponseEntity.ok(dtoMapper.toResponse(domain));
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/bypersonaId/{personaId}")
	public ResponseEntity<PersonaResponse> findByPersonaId(@PathVariable BigDecimal personaId) {
		try {
			Persona domain = service.findByPersonaId(personaId);
			return ResponseEntity.ok(dtoMapper.toResponse(domain));
		} catch (PersonaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/{uniqueId}")
	public ResponseEntity<PersonaResponse> findByUniqueId(@PathVariable Long uniqueId) {
		try {
			Persona domain = service.findByUniqueId(uniqueId);
			return ResponseEntity.ok(dtoMapper.toResponse(domain));
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

	@GetMapping("/deudaExamen/facultad/{facultadId}/persona/{personaId}/{documentoId}/fecha/{fechaExamen}")
	public ResponseEntity<DeudaExamenResponse> getDeudaExamenByFacultadAndPersona(@PathVariable Integer facultadId, @PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaExamen) {
		return ResponseEntity.ok(dtoMapper.toDeudaExamenResponse(service.getDeudaExamenByFacultadAndPersona(facultadId, personaId, documentoId, fechaExamen)));
	}

	@PostMapping("/")
	public ResponseEntity<PersonaResponse> add(@RequestBody PersonaRequest personaRequest) {
		Persona domain = dtoMapper.toDomain(personaRequest);
		Persona created = service.create(domain);
		return ResponseEntity.ok(dtoMapper.toResponse(created));
	}

	@PutMapping("/{uniqueId}")
	public ResponseEntity<PersonaResponse> update(@RequestBody PersonaRequest personaRequest, @PathVariable Long uniqueId) {
		Persona domain = dtoMapper.toDomain(personaRequest);
		Persona updated = service.update(domain, uniqueId);
		return ResponseEntity.ok(dtoMapper.toResponse(updated));
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
