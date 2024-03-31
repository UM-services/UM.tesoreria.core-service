/**
 * 
 */
package um.tesoreria.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.Postal;
import um.tesoreria.core.service.PostalService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/postal")
public class PostalController {
	@Autowired
	private PostalService service;
	
	@GetMapping("/{codigopostal}")
	public ResponseEntity<Postal> findByCodigopostal(@PathVariable Integer codigopostal) {
		return new ResponseEntity<Postal>(service.findByCodigopostal(codigopostal), HttpStatus.OK);
	}
}
