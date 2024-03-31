/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.LectivoTotal;
import um.tesoreria.core.service.LectivoTotalService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/lectivototal")
public class LectivoTotalController {

	@Autowired
	private LectivoTotalService service;

	@GetMapping("/tipo/{facultadId}/{lectivoId}/{tipoChequeraId}")
	public ResponseEntity<List<LectivoTotal>> findAllByTipo(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipoChequeraId) {
		return new ResponseEntity<List<LectivoTotal>>(service.findAllByTipo(facultadId, lectivoId, tipoChequeraId),
				HttpStatus.OK);
	}

	@GetMapping("/producto/{facultadId}/{lectivoId}/{tipoChequeraId}/{productoId}")
	public ResponseEntity<LectivoTotal> findByProducto(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipoChequeraId, @PathVariable Integer productoId) {
		return new ResponseEntity<LectivoTotal>(
				service.findByProducto(facultadId, lectivoId, tipoChequeraId, productoId), HttpStatus.OK);
	}

}
