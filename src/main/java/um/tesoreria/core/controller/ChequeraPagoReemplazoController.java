/**
 * 
 */
package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.ChequeraPagoReemplazo;
import um.tesoreria.core.service.ChequeraPagoReemplazoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequerapagoreemplazo")
public class ChequeraPagoReemplazoController {

	private final ChequeraPagoReemplazoService service;

	public ChequeraPagoReemplazoController(ChequeraPagoReemplazoService service) {
		this.service = service;
	}

	@GetMapping("/{chequeraPagoReemplazoId}")
	public ResponseEntity<ChequeraPagoReemplazo> findByChequeraPagoReemplazoId(@PathVariable Long chequeraPagoReemplazoId) {
		return new ResponseEntity<>(service.findByChequeraPagoReemplazoId(chequeraPagoReemplazoId), HttpStatus.OK);
	}
}
