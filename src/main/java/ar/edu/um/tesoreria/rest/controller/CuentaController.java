/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.CuentaException;
import ar.edu.um.tesoreria.rest.model.Cuenta;
import ar.edu.um.tesoreria.rest.model.view.CuentaSearch;
import ar.edu.um.tesoreria.rest.service.CuentaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cuenta")
public class CuentaController {

	@Autowired
	private CuentaService service;

	@GetMapping("/")
	public ResponseEntity<List<Cuenta>> findAll() {
		return new ResponseEntity<List<Cuenta>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/cierreresultado")
	public ResponseEntity<List<Cuenta>> findAllByCierreResultado() {
		return new ResponseEntity<List<Cuenta>>(service.findAllByCierreResultado(), HttpStatus.OK);
	}

	@GetMapping("/cierreactivopasivo")
	public ResponseEntity<List<Cuenta>> findAllByCierreActivoPasivo() {
		return new ResponseEntity<List<Cuenta>>(service.findAllByCierreActivoPasivo(), HttpStatus.OK);
	}

	@PostMapping("/search/{visible}")
	public ResponseEntity<List<CuentaSearch>> findByStrings(@RequestBody List<String> conditions,
			@PathVariable Boolean visible) {
		return new ResponseEntity<List<CuentaSearch>>(service.findByStrings(conditions, visible), HttpStatus.OK);
	}

	@GetMapping("/{numeroCuenta}")
	public ResponseEntity<Cuenta> findByCuenta(@PathVariable BigDecimal numeroCuenta) {
		try {
			return new ResponseEntity<Cuenta>(service.findByNumeroCuenta(numeroCuenta), HttpStatus.OK);
		} catch (CuentaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/id/{cuentaContableId}")
	public ResponseEntity<Cuenta> findByCuentaContableId(@PathVariable Long cuentaContableId) {
		try {
			return new ResponseEntity<Cuenta>(service.findByCuentaContableId(cuentaContableId), HttpStatus.OK);
		} catch (CuentaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<Cuenta> add(@RequestBody Cuenta cuenta) {
		return new ResponseEntity<Cuenta>(service.add(cuenta), HttpStatus.OK);
	}

	@PutMapping("/{numeroCuenta}")
	public ResponseEntity<Cuenta> update(@RequestBody Cuenta cuenta, @PathVariable BigDecimal numeroCuenta) {
		return new ResponseEntity<Cuenta>(service.update(cuenta, numeroCuenta), HttpStatus.OK);
	}

	@DeleteMapping("/{numeroCuenta}")
	public ResponseEntity<Void> delete(@PathVariable BigDecimal numeroCuenta) {
		service.delete(numeroCuenta);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/recalculagrados")
	public ResponseEntity<String> recalculaGrados() {
		return new ResponseEntity<String>(service.recalculaGrados(), HttpStatus.OK);
	}

}
