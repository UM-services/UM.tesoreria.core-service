/**
 * 
 */
package um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.model.ChequeraPagoReemplazo;
import um.tesoreria.rest.service.ChequeraPagoReemplazoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequerapagoreemplazo")
public class ChequeraPagoReemplazoController {

	@Autowired
	private ChequeraPagoReemplazoService service;

	@GetMapping("/{chequerapagoreemplazoId}")
	public ResponseEntity<ChequeraPagoReemplazo> findById(@PathVariable Long chequerapagoreemplazoId) {
		return new ResponseEntity<ChequeraPagoReemplazo>(service.findById(chequerapagoreemplazoId), HttpStatus.OK);
	}
}
