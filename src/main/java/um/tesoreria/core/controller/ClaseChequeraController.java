/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.ClaseChequera;
import um.tesoreria.core.service.ClaseChequeraService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/clasechequera")
public class ClaseChequeraController {

	@Autowired
	private ClaseChequeraService service;
	
	@GetMapping("/")
	public ResponseEntity<List<ClaseChequera>> findAll() {
		return new ResponseEntity<List<ClaseChequera>>(service.findAll(), HttpStatus.OK);
	}
}
