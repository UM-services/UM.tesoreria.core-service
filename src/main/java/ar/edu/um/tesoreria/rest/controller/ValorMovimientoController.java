/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.ValorMovimiento;
import ar.edu.um.tesoreria.rest.service.ValorMovimientoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/valormovimiento")
public class ValorMovimientoController {
	@Autowired
	private ValorMovimientoService service;
	
	@GetMapping("/numero/{valorID}/{numero}")
	public ResponseEntity<ValorMovimiento> findByNumero(@PathVariable Integer valorID, @PathVariable Long numero) {
		return new ResponseEntity<ValorMovimiento>(service.findByNumero(valorID, numero), HttpStatus.OK);
	}

}
