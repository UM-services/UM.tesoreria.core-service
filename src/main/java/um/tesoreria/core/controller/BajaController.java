/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.Baja;
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

import um.tesoreria.core.exception.BajaException;
import um.tesoreria.core.service.BajaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/baja", "/api/tesoreria/core/baja"})
@RequiredArgsConstructor
public class BajaController {

	private final BajaService service;

	@GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<Baja> findByUnique(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
											 @PathVariable Long chequeraSerieId) {
		try {
            return ResponseEntity.ok(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId));
		} catch (BajaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<Baja> add(@RequestBody Baja baja) {
        return ResponseEntity.ok(service.add(baja));
	}

	@PutMapping("/{bajaId}")
	public ResponseEntity<Baja> update(@RequestBody Baja baja, @PathVariable Long bajaId) {
        return ResponseEntity.ok(service.update(baja, bajaId));
	}

	@DeleteMapping("/unique/{facultadId}/{tipochequeraId}/{chequeraserieId}")
	public ResponseEntity<Void> deleteByUnique(@PathVariable Integer facultadId, @PathVariable Integer tipochequeraId,
			@PathVariable Long chequeraserieId) {
		service.deleteByUnique(facultadId, tipochequeraId, chequeraserieId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
