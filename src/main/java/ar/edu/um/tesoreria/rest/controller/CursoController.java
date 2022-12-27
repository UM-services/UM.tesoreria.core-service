/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.CursoNotFoundException;
import ar.edu.um.tesoreria.rest.model.Curso;
import ar.edu.um.tesoreria.rest.service.CursoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/curso")
public class CursoController {

	@Autowired
	private CursoService service;

	@GetMapping("/top/{claseChequeraId}")
	public ResponseEntity<Curso> findTopByClaseChequeraId(@PathVariable Integer claseChequeraId) {
		try {
			return new ResponseEntity<Curso>(service.findTopByClaseChequera(claseChequeraId), HttpStatus.OK);
		} catch (CursoNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
