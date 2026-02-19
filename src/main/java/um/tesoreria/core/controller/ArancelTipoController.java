/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import jakarta.annotation.Resource;
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
import um.tesoreria.core.exception.ArancelTipoException;
import um.tesoreria.core.kotlin.model.ArancelTipo;
import um.tesoreria.core.model.view.ArancelTipoLectivo;
import um.tesoreria.core.service.ArancelTipoService;

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
	public ResponseEntity<List<ArancelTipo>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/lectivo/{lectivoId}")
	public ResponseEntity<List<ArancelTipoLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
		return ResponseEntity.ok(service.findAllByLectivoId(lectivoId));
	}

	@GetMapping("/{arancelTipoId}")
	public ResponseEntity<ArancelTipo> findByArancelTipoId(@PathVariable Integer arancelTipoId) {
		try {
			return ResponseEntity.ok(service.findByArancelTipoId(arancelTipoId));
		} catch (ArancelTipoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/completo/{arancelTipoIdCompleto}")
	public ResponseEntity<ArancelTipo> findByArancelTipoIdCompleto(@PathVariable Integer arancelTipoIdCompleto) {
		try {
			return ResponseEntity.ok(service.findByArancelTipoIdCompleto(arancelTipoIdCompleto));
		} catch (ArancelTipoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/last")
	public ResponseEntity<ArancelTipo> findLast() {
		return ResponseEntity.ok(service.findLast());
	}

	@PostMapping("/")
	public ResponseEntity<ArancelTipo> add(@RequestBody ArancelTipo arancelTipo) {
		return ResponseEntity.ok(service.add(arancelTipo));
	}

	@PutMapping("/{arancelTipoId}")
	public ResponseEntity<ArancelTipo> update(@RequestBody ArancelTipo arancelTipo,
			@PathVariable Integer arancelTipoId) {
		return ResponseEntity.ok(service.update(arancelTipo, arancelTipoId));
	}

	@DeleteMapping("/{arancelTipoId}")
	public ResponseEntity<Void> deleteByAranceltipoId(@PathVariable Integer arancelTipoId) {
		service.deleteByArancelTipoId(arancelTipoId);
		return ResponseEntity.noContent().build();
	}
}
