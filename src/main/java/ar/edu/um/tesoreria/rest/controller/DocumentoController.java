/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Documento;
import ar.edu.um.tesoreria.rest.service.DocumentoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/documento")
public class DocumentoController {

	@Autowired
	private DocumentoService service;

	@GetMapping("/")
	public ResponseEntity<List<Documento>> findAll() {
		return new ResponseEntity<List<Documento>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/{documentoId}")
	public ResponseEntity<Documento> findByDocumentoId(@PathVariable Integer documentoId) {
		return new ResponseEntity<Documento>(service.findByDocumentoId(documentoId), HttpStatus.OK);
	}

}
