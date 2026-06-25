/**
 * 
 */
package um.tesoreria.core.hexagonal.arancelTipo.infrastructure.web.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
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
import um.tesoreria.core.hexagonal.arancelTipo.application.exception.ArancelTipoException;
import um.tesoreria.core.hexagonal.arancelTipo.infrastructure.persistence.entity.ArancelTipoEntity;
import um.tesoreria.core.model.view.ArancelTipoLectivo;
import um.tesoreria.core.hexagonal.arancelTipo.application.service.ArancelTipoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/aranceltipo")
@RequiredArgsConstructor
public class ArancelTipoController {

	private final ArancelTipoService service;

	@GetMapping("/")
	public ResponseEntity<List<ArancelTipoEntity>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/habilitados")
	public ResponseEntity<List<ArancelTipoEntity>> findAllHabilitados() {
		return ResponseEntity.ok(service.findAllHabilitados());
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<ArancelTipoLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
		return ResponseEntity.ok(service.findAllByLectivoId(lectivoId));
	}

	@GetMapping("/{arancelTipoId}")
	public ResponseEntity<ArancelTipoEntity> findByArancelTipoId(@PathVariable Integer arancelTipoId) {
		try {
			return ResponseEntity.ok(service.findByArancelTipoId(arancelTipoId));
		} catch (ArancelTipoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/completo/{arancelTipoIdCompleto}")
	public ResponseEntity<ArancelTipoEntity> findByArancelTipoIdCompleto(@PathVariable Integer arancelTipoIdCompleto) {
		try {
			return ResponseEntity.ok(service.findByArancelTipoIdCompleto(arancelTipoIdCompleto));
		} catch (ArancelTipoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/last")
	public ResponseEntity<ArancelTipoEntity> findLast() {
		return ResponseEntity.ok(service.findLast());
	}

	@PostMapping("/")
	public ResponseEntity<ArancelTipoEntity> add(@RequestBody ArancelTipoEntity arancelTipo) {
		return ResponseEntity.ok(service.add(arancelTipo));
	}

	@PutMapping("/{arancelTipoId}")
	public ResponseEntity<ArancelTipoEntity> update(@RequestBody ArancelTipoEntity arancelTipo,
	                                                @PathVariable Integer arancelTipoId) {
		return ResponseEntity.ok(service.update(arancelTipo, arancelTipoId));
	}

	@DeleteMapping("/{arancelTipoId}")
	public ResponseEntity<Void> deleteByAranceltipoId(@PathVariable Integer arancelTipoId) {
		service.deleteByArancelTipoId(arancelTipoId);
		return ResponseEntity.noContent().build();
	}
}
