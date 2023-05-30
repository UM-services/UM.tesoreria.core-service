/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;

import um.tesoreria.rest.model.dto.ProveedorArticuloAsignable;
import um.tesoreria.rest.service.ProveedorArticuloService;
import um.tesoreria.rest.kotlin.model.ProveedorArticulo;

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
		return new ResponseEntity<List<ProveedorArticulo>>(service.findAllByProveedorMovimientoIds(
				asignables.getProveedorMovimientoIds(), asignables.getAsignables()), HttpStatus.OK);
	}

}
