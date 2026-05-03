/**
 * 
 */
package um.tesoreria.core.hexagonal.articulo.infrastructure.web.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
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

import um.tesoreria.core.exception.ArticuloException;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;
import um.tesoreria.core.model.view.ArticuloKey;
import um.tesoreria.core.hexagonal.articulo.application.service.ArticuloService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/articulo")
@RequiredArgsConstructor
public class ArticuloController {

	private final ArticuloService service;

	@GetMapping("/")
	public ResponseEntity<List<ArticuloEntity>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}

	@PostMapping("/search")
	public ResponseEntity<List<ArticuloKey>> findByStrings(@RequestBody List<String> conditions) {
		return new ResponseEntity<>(service.findByStrings(conditions), HttpStatus.OK);
	}

	@GetMapping("/{articuloId}")
	public ResponseEntity<ArticuloEntity> findByArticuloId(@PathVariable Long articuloId) {
		try {
			return new ResponseEntity<>(service.findByArticuloId(articuloId), HttpStatus.OK);
		} catch (ArticuloException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/new")
	public ResponseEntity<ArticuloEntity> articuloNew() {
		try {
			return new ResponseEntity<>(service.articuloNew(), HttpStatus.OK);
		} catch (ArticuloException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/")
	public ResponseEntity<ArticuloEntity> add(@RequestBody ArticuloEntity articulo) {
		return new ResponseEntity<>(service.add(articulo), HttpStatus.OK);
	}
	
	@PutMapping("/{articuloId}")
	public ResponseEntity<ArticuloEntity> update(@RequestBody ArticuloEntity articulo, @PathVariable Long articuloId) {
		return new ResponseEntity<>(service.update(articulo, articuloId), HttpStatus.OK);
	}

}
