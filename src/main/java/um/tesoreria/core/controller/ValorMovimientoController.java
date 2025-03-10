/**
 * 
 */
package um.tesoreria.core.controller;

import um.tesoreria.core.exception.ValorMovimientoException;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.ValorMovimientoService;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/valorMovimiento")
public class ValorMovimientoController {

	@Autowired
	private ValorMovimientoService service;

	@GetMapping("/{valorMovimientoId}")
	public ResponseEntity<ValorMovimiento> findByValorMovimientoId(@PathVariable Long valorMovimientoId) {
		try {
			return new ResponseEntity<ValorMovimiento>(service.findByValorMovimientoId(valorMovimientoId), HttpStatus.OK);
		} catch (ValorMovimientoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
	
	@GetMapping("/numero/{valorId}/{numero}")
	public ResponseEntity<ValorMovimiento> findByNumero(@PathVariable Integer valorId, @PathVariable Long numero) {
		try {
			return new ResponseEntity<ValorMovimiento>(service.findByNumero(valorId, numero), HttpStatus.OK);
		} catch (ValorMovimientoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/banco/{valorId}/{numero}/{bancariaId}")
	public ResponseEntity<ValorMovimiento> findByBanco(@PathVariable Integer valorId, @PathVariable Long numero, @PathVariable Long bancariaId) {
		try {
			return new ResponseEntity<ValorMovimiento>(service.findByBanco(valorId, numero, bancariaId), HttpStatus.OK);
		} catch (ValorMovimientoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
