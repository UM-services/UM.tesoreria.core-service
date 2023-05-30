/**
 * 
 */
package um.tesoreria.rest.controller;

import um.tesoreria.rest.kotlin.model.Usuario;
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

import um.tesoreria.rest.exception.UsuarioException;
import um.tesoreria.rest.service.UsuarioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/usuario")
public class UsuarioController {

	@Autowired
	private UsuarioService service;

	@GetMapping("/usuario/{login}")
	public ResponseEntity<Usuario> findByLogin(@PathVariable String login) {
		try {
			return new ResponseEntity<Usuario>(service.findByLogin(login), HttpStatus.OK);
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/usuario")
	public ResponseEntity<Usuario> add(@RequestBody Usuario usuario) {
		return new ResponseEntity<Usuario>(service.add(usuario), HttpStatus.OK);
	}

	@PutMapping("/usuario/{userId}")
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario, @PathVariable Long userId) {
		return new ResponseEntity<Usuario>(service.update(usuario, userId), HttpStatus.OK);
	}

	@PutMapping("/password")
	public ResponseEntity<Usuario> findByPassword(@RequestBody Usuario usuario) {
		try {
			return new ResponseEntity<Usuario>(service.findByPassword(usuario.getPassword()), HttpStatus.OK);
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/lastLog/{userId}")
	public ResponseEntity<Usuario> updateLastLog(@PathVariable Long userId) {
		try {
			return new ResponseEntity<Usuario>(service.updateLastLog(userId), HttpStatus.OK);
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
