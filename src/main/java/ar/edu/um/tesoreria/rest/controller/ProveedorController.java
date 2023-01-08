/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

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

import ar.edu.um.tesoreria.rest.exception.ProveedorNotFoundException;
import ar.edu.um.tesoreria.rest.model.Proveedor;
import ar.edu.um.tesoreria.rest.model.view.ProveedorSearch;
import ar.edu.um.tesoreria.rest.service.ProveedorService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedor")
public class ProveedorController {

	@Autowired
	private ProveedorService service;

	@GetMapping("/")
	public ResponseEntity<List<Proveedor>> findAll() {
		return new ResponseEntity<List<Proveedor>>(service.findAll(), HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<ProveedorSearch>> findAllByStrings(@RequestBody List<String> conditions) {
		return new ResponseEntity<List<ProveedorSearch>>(service.findAllByStrings(conditions), HttpStatus.OK);
	}

	@GetMapping("/{proveedorId}")
	public ResponseEntity<Proveedor> findByProveedorId(@PathVariable Integer proveedorId) {
		try {
			return new ResponseEntity<Proveedor>(service.findByProveedorId(proveedorId), HttpStatus.OK);
		} catch (ProveedorNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/cuit/{cuit}")
	public ResponseEntity<Proveedor> findByCuit(@PathVariable String cuit) {
		try {
			return new ResponseEntity<Proveedor>(service.findByCuit(cuit), HttpStatus.OK);
		} catch (ProveedorNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/last")
	public ResponseEntity<Proveedor> findLast() {
		return new ResponseEntity<Proveedor>(service.findLast(), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<Proveedor> add(@RequestBody Proveedor proveedor) {
		return new ResponseEntity<Proveedor>(service.add(proveedor), HttpStatus.OK);
	}

	@PutMapping("/{proveedorId}")
	public ResponseEntity<Proveedor> update(@RequestBody Proveedor proveedor, @PathVariable Integer proveedorId) {
		return new ResponseEntity<Proveedor>(service.update(proveedor, proveedorId), HttpStatus.OK);
	}

	@DeleteMapping("/{proveedorId}")
	public ResponseEntity<Void> delete(@PathVariable Integer proveedorId) {
		service.delete(proveedorId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
