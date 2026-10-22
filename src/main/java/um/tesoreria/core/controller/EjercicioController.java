/**
 * 
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
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

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.EjercicioException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.CreateEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.DeleteEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.UpdateEjercicioUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto.EjercicioRequest;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto.EjercicioResponse;
import um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.mapper.EjercicioDtoMapper;
import um.tesoreria.core.kotlin.model.Ejercicio;
import um.tesoreria.core.service.EjercicioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ejercicio")
@RequiredArgsConstructor
public class EjercicioController {
	
	private final EjercicioService service;
	private final CreateEjercicioUseCase createUseCase;
	private final UpdateEjercicioUseCase updateUseCase;
	private final DeleteEjercicioUseCase deleteUseCase;
	private final EjercicioDtoMapper dtoMapper;

	@GetMapping("/")
	public ResponseEntity<List<Ejercicio>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}
	
	@GetMapping("/{ejercicioId}")
	public ResponseEntity<Ejercicio> findByEjercicioId(@PathVariable Integer ejercicioId) {
		try {
			return ResponseEntity.ok(service.findByEjercicioId(ejercicioId));
		} catch (EjercicioException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	@GetMapping("/last")
	public ResponseEntity<Ejercicio> findLast() {
		return ResponseEntity.ok(service.findLast());
	}
	
	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<Ejercicio> findByFecha(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha) {
		try {
			return ResponseEntity.ok(service.findByFecha(fecha));
		} catch (EjercicioException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
	}
	
	// Escrituras: fachada del slice contable/ejercicio; sus errores los responde EjercicioExceptionAdvice.
	@PostMapping("/")
	public ResponseEntity<EjercicioResponse> add(@RequestBody EjercicioRequest request) {
		return ResponseEntity.ok(dtoMapper.toResponse(createUseCase.createEjercicio(dtoMapper.toDomain(request))));
	}
	
	@PutMapping("/{ejercicioId}")
	public ResponseEntity<EjercicioResponse> update(@RequestBody EjercicioRequest request, @PathVariable Integer ejercicioId) {
		return ResponseEntity.ok(dtoMapper.toResponse(
				updateUseCase.updateEjercicio(ejercicioId, dtoMapper.toDomain(request))));
	}
	
	@DeleteMapping("/{ejercicioId}")
	public ResponseEntity<Void> delete(@PathVariable Integer ejercicioId) {
		deleteUseCase.deleteEjercicio(ejercicioId);
		return ResponseEntity.noContent().build();
	}

}
