/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;

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

import um.tesoreria.core.model.UsuarioLdap;
import um.tesoreria.core.service.UsuarioLdapService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/usuarioldap")
public class UsuarioLdapController {
	@Autowired
	private UsuarioLdapService service;
	
	@GetMapping("/documento/{documento}")
	public ResponseEntity<UsuarioLdap> findByDocumento(@PathVariable BigDecimal documento) {
		return new ResponseEntity<UsuarioLdap>(service.findByDocumento(documento), HttpStatus.OK);
	}
	
	@PostMapping("/")
	public ResponseEntity<UsuarioLdap> add(@RequestBody UsuarioLdap usuarioldap) {
		return new ResponseEntity<UsuarioLdap>(service.add(usuarioldap), HttpStatus.OK);
	}
	@GetMapping("/add")
	public ResponseEntity<UsuarioLdap> addByGet(@RequestParam BigDecimal documento, @RequestParam String evento, @RequestParam String estado, @RequestParam String facultad) {
		return add(new UsuarioLdap(null, documento, evento, estado, facultad));
	}
}
