/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.rest.exception.ArticuloException;
import um.tesoreria.rest.kotlin.model.Articulo;
import um.tesoreria.rest.model.view.ArticuloKey;
import um.tesoreria.rest.service.ArticuloService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/articulo")
public class ArticuloController {

	@Autowired
	private ArticuloService service;

	@GetMapping("/")
	public ResponseEntity<List<Articulo>> findAll() {
		return new ResponseEntity<List<Articulo>>(service.findAll(), HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<ArticuloKey>> findByStrings(@RequestBody List<String> conditions) {
		return new ResponseEntity<List<ArticuloKey>>(service.findByStrings(conditions), HttpStatus.OK);
	}

	@GetMapping("/{articuloId}")
	public ResponseEntity<Articulo> findByArticuloId(@PathVariable Long articuloId) {
		try {
			return new ResponseEntity<Articulo>(service.findByArticuloId(articuloId), HttpStatus.OK);
		} catch (ArticuloException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/new")
	public ResponseEntity<Articulo> articuloNew() {
		try {
			return new ResponseEntity<Articulo>(service.articuloNew(), HttpStatus.OK);
		} catch (ArticuloException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<Articulo> add(@RequestBody Articulo articulo) {
		return new ResponseEntity<Articulo>(service.add(articulo), HttpStatus.OK);
	}
	
	@PutMapping("/{articuloId}")
	public ResponseEntity<Articulo> update(@RequestBody Articulo articulo, @PathVariable Long articuloId) {
		return new ResponseEntity<Articulo>(service.update(articulo, articuloId), HttpStatus.OK);
	}

}
