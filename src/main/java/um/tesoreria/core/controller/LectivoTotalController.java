/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.LectivoTotalException;
import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.LectivoTotalService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/lectivototal")
@RequiredArgsConstructor
public class LectivoTotalController {

	private final LectivoTotalService service;

	@GetMapping("/tipo/{facultadId}/{lectivoId}/{tipoChequeraId}")
	public ResponseEntity<List<LectivoTotal>> findAllByTipo(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipoChequeraId) {
        return ResponseEntity.ok(service.findAllByTipo(facultadId, lectivoId, tipoChequeraId));
	}

	@GetMapping("/producto/{facultadId}/{lectivoId}/{tipoChequeraId}/{productoId}")
	public ResponseEntity<LectivoTotal> findByProducto(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipoChequeraId, @PathVariable Integer productoId) {
        try {
            return ResponseEntity.ok(service.findByProducto(facultadId, lectivoId, tipoChequeraId, productoId));
        } catch (LectivoTotalException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

}
