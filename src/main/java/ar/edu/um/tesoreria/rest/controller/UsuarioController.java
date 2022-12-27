/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

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

import ar.edu.um.tesoreria.rest.exception.UsuarioNotFoundException;
import ar.edu.um.tesoreria.rest.model.Usuario;
import ar.edu.um.tesoreria.rest.service.UsuarioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<Usuario> findByUsuarioId(@PathVariable String usuarioId) {
		try {
			return new ResponseEntity<Usuario>(service.findByUsuarioId(usuarioId), HttpStatus.OK);
		} catch (UsuarioNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/usuario")
	public ResponseEntity<Usuario> add(@RequestBody Usuario usuario) {
		return new ResponseEntity<Usuario>(service.add(usuario), HttpStatus.OK);
	}

	@PutMapping("/usuario/{usuarioId}")
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario, @PathVariable String usuarioId) {
		return new ResponseEntity<Usuario>(service.update(usuario, usuarioId), HttpStatus.OK);
	}

	@PutMapping("/password")
	public ResponseEntity<Usuario> findByPassword(@RequestBody Usuario usuario) {
		try {
			return new ResponseEntity<Usuario>(service.findByPassword(usuario.getPassword()), HttpStatus.OK);
		} catch (UsuarioNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/lastLog/{usuarioId}")
	public ResponseEntity<Usuario> updateLastLog(@PathVariable String usuarioId) {
		try {
			return new ResponseEntity<Usuario>(service.updateLastLog(usuarioId), HttpStatus.OK);
		} catch (UsuarioNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
