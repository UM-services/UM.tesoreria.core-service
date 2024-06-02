/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import um.tesoreria.core.model.dto.ProveedorArticuloAsignable;
import um.tesoreria.core.service.ProveedorArticuloService;
import um.tesoreria.core.kotlin.model.ProveedorArticulo;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedorArticulo")
public class ProveedorArticuloController {

	@Autowired
	private ProveedorArticuloService service;

	@PostMapping("/movimiento/")
	public ResponseEntity<List<ProveedorArticulo>> findAllByProveedorMovimientoIds(
			@RequestBody ProveedorArticuloAsignable asignables) throws JsonProcessingException {
		return new ResponseEntity<>(service.findAllByProveedorMovimientoIds(
				asignables.getProveedorMovimientoIds(), asignables.getAsignables()), HttpStatus.OK);
	}

}
