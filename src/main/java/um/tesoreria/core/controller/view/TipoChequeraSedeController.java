/**
 * 
 */
package um.tesoreria.core.controller.view;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.view.TipoChequeraSedeException;
import um.tesoreria.core.model.view.TipoChequeraSede;
import um.tesoreria.core.service.view.TipoChequeraSedeService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/tipochequerasede")
@RequiredArgsConstructor
public class TipoChequeraSedeController {

	private final TipoChequeraSedeService service;

	@GetMapping("/")
	public ResponseEntity<List<TipoChequeraSede>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/sede/{geograficaId}/{modulo}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByGeograficaId(@PathVariable Integer geograficaId,
			@PathVariable String modulo) {
		return ResponseEntity.ok(service.findAllByGeograficaId(geograficaId, modulo));
	}

	@GetMapping("/lectivo/{lectivoId}/{facultadId}/{geograficaId}/{modulo}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByGeograficaId(@PathVariable Integer lectivoId,
			@PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable String modulo) {
		return ResponseEntity.ok(service.findAllByLectivoIdAndFacultadId(lectivoId, facultadId, geograficaId, modulo));
	}

	@GetMapping("/persona/{personaId}/{documentoId}/{lectivoId}/{facultadId}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByPersonaId(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId, @PathVariable Integer lectivoId, @PathVariable Integer facultadId) {
		return ResponseEntity.ok(service.findAllByPersonaId(personaId, documentoId, lectivoId, facultadId));
	}

	@GetMapping("/tablero/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<TipoChequeraSede>> findAllByLectivoIdAndGeograficaId(@PathVariable Integer lectivoId,
			@PathVariable Integer geograficaId) {
		return ResponseEntity.ok(service.findAllByLectivoIdAndGeograficaId(lectivoId, geograficaId));
	}

	@GetMapping("/{tipoChequeraId}")
	public ResponseEntity<TipoChequeraSede> findByTipoChequeraId(@PathVariable Integer tipoChequeraId) {
		try {
			return ResponseEntity.ok(service.findByTipoChequeraId(tipoChequeraId));
		} catch (TipoChequeraSedeException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
