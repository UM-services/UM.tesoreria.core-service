/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.UsuarioLdapException;
import um.tesoreria.core.model.UsuarioLdap;
import um.tesoreria.core.service.UsuarioLdapService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/usuarioldap")
@RequiredArgsConstructor
public class UsuarioLdapController {

	private final UsuarioLdapService service;
	
	@GetMapping("/documento/{documento}")
	public ResponseEntity<UsuarioLdap> findByDocumento(@PathVariable BigDecimal documento) {
        try {
            return ResponseEntity.ok(service.findByDocumento(documento));
        } catch (UsuarioLdapException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}
	
	@PostMapping("/")
	public ResponseEntity<UsuarioLdap> add(@RequestBody UsuarioLdap usuarioldap) {
        return ResponseEntity.ok(service.add(usuarioldap));
	}
	@GetMapping("/add")
	public ResponseEntity<UsuarioLdap> addByGet(@RequestParam BigDecimal documento, @RequestParam String evento, @RequestParam String estado, @RequestParam String facultad) {
		return add(new UsuarioLdap(null, documento, evento, estado, facultad));
	}
}
