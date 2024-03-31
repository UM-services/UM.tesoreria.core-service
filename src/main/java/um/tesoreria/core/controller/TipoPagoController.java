/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import um.tesoreria.core.kotlin.model.TipoPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.TipoPagoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/tipopago")
public class TipoPagoController {

	@Autowired
	private TipoPagoService service;

	@GetMapping("/")
	public ResponseEntity<List<TipoPago>> findAll() {
		return new ResponseEntity<List<TipoPago>>(service.findAll(), HttpStatus.OK);
	}
}
