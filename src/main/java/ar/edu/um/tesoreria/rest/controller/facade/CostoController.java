/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.facade;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.dto.AsignacionCosto;
import ar.edu.um.tesoreria.rest.service.facade.CostoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/costo")
@Slf4j
public class CostoController {

	@Autowired
	private CostoService service;

	@PostMapping("/addAsignacion")
	public ResponseEntity<Boolean> addAsignacion(@RequestBody AsignacionCosto asignacionCosto) {
		log.debug("AsignacionCosto - {}", asignacionCosto);
		return new ResponseEntity<Boolean>(service.addAsignacion(asignacionCosto), HttpStatus.OK);
	}

	@GetMapping("/deleteDesignacion/{entregaId}")
	public ResponseEntity<Boolean> deleteAsignacion(@PathVariable Long entregaId) {
		log.debug("DeleteDesignacion - {}", entregaId);
		return new ResponseEntity<Boolean>(service.deleteDesignacion(entregaId), HttpStatus.OK);
	}

	@GetMapping("/depurarGastosProveedor/{proveedorId}/{geograficaId}")
	public ResponseEntity<Boolean> depurarGastosProveedor(@PathVariable Integer proveedorId,
			@PathVariable Integer geograficaId) {
		log.debug("DepurarGastosProveedor - {}/{}", proveedorId, geograficaId);
		return new ResponseEntity<Boolean>(service.depurarGastosProveedor(proveedorId, geograficaId), HttpStatus.OK);
	}

}
