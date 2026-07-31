/**
 * 
 */
package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.TipoChequeraLectivoSede;
import um.tesoreria.core.service.view.TipoChequeraLectivoSedeService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/tipochequeralectivosede")
@RequiredArgsConstructor
public class TipoChequeraLectivoSedeController {
	
	private final TipoChequeraLectivoSedeService service;

	@GetMapping("/disenho/{facultadId}/{lectivoId}/{geograficaId}")
	public ResponseEntity<List<TipoChequeraLectivoSede>> findAllByDisenho(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer geograficaId) {
		return ResponseEntity.ok(
				service.findAllByDisenho(facultadId, lectivoId, geograficaId));
	}
}
