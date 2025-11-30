/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.Usuario;
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

import um.tesoreria.core.exception.UsuarioException;
import um.tesoreria.core.service.UsuarioService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/usuario", "/api/tesoreria/core/usuario"})
@RequiredArgsConstructor
public class UsuarioController {

	private final UsuarioService service;

	@GetMapping("/usuario/{login}")
	public ResponseEntity<Usuario> findByLogin(@PathVariable String login) {
		try {
			return new ResponseEntity<>(service.findByLogin(login), HttpStatus.OK);
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@PostMapping("/usuario")
	public ResponseEntity<Usuario> add(@RequestBody Usuario usuario) {
        return ResponseEntity.ok(service.add(usuario));
	}

	@PutMapping("/usuario/{userId}")
	public ResponseEntity<Usuario> update(@RequestBody Usuario usuario, @PathVariable Long userId) {
        return ResponseEntity.ok(service.update(usuario, userId));
	}

	@PutMapping("/password")
	public ResponseEntity<Usuario> findByPassword(@RequestBody Usuario usuario) {
		try {
            return ResponseEntity.ok(service.findByPassword(usuario.getPassword()));
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/lastLog/{userId}")
	public ResponseEntity<Usuario> updateLastLog(@PathVariable Long userId) {
		try {
            return ResponseEntity.ok(service.updateLastLog(userId));
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/google/mail/{googleMail}")
	public ResponseEntity<Usuario> findByGoogleMail(@PathVariable String googleMail) {
		try {
			return ResponseEntity.ok(service.findByGoogleMail(googleMail));
		} catch (UsuarioException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
