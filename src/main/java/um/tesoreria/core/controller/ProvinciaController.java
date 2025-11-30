/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ProvinciaException;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.service.ProvinciaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/provincia", "/api/tesoreria/core/provincia"})
@RequiredArgsConstructor
public class ProvinciaController {

	private final ProvinciaService service;

	@GetMapping("/facultad/{facultadId}")
	public ResponseEntity<List<Provincia>> findAllByFacultadId(@PathVariable Integer facultadId) {
        return ResponseEntity.ok(service.findAllByFacultadId(facultadId));
	}

	@GetMapping("/{uniqueId}")
	public ResponseEntity<Provincia> findByUnicoId(@PathVariable Long uniqueId) {
        try {
            return ResponseEntity.ok(service.findByUniqueId(uniqueId));
        }
        catch (ProvinciaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

	@GetMapping("/unique/{facultadId}/{provinciaId}")
	public ResponseEntity<Provincia> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer provinciaId) {
        try {
            return ResponseEntity.ok(service.findByUnique(facultadId, provinciaId));
        }
        catch (ProvinciaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

	@GetMapping("/nombre/{facultadId}")
	public ResponseEntity<Provincia> findByNombre(@PathVariable Integer facultadId,
			@RequestParam("nombre") String nombre) {
        try {
            return ResponseEntity.ok(service.findByNombre(facultadId, nombre));
        }
        catch (ProvinciaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

	@GetMapping("/last/{facultadId}")
	public ResponseEntity<Provincia> findLastByFacultadId(@PathVariable Integer facultadId) {
        try {
            return ResponseEntity.ok(service.findLastByFacultadId(facultadId));
        }
        catch (ProvinciaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

}
