/**
 * 
 */
package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.persistence.entity.ClaseChequeraEntity;
import um.tesoreria.core.hexagonal.chequera.claseChequera.application.service.ClaseChequeraService;

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
	public ResponseEntity<List<ClaseChequeraEntity>> findAll() {
		return new ResponseEntity<List<ClaseChequeraEntity>>(service.findAll(), HttpStatus.OK);
	}
}
