/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.Documento;
import um.tesoreria.core.service.DocumentoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/documento")
public class DocumentoController {

	private final DocumentoService service;

	public DocumentoController(DocumentoService service) {
		this.service = service;
	}

	@GetMapping("/")
	public ResponseEntity<List<Documento>> findAll() {
		return new ResponseEntity<List<Documento>>(service.findAll(), HttpStatus.OK);
	}

	@GetMapping("/{documentoId}")
	public ResponseEntity<Documento> findByDocumentoId(@PathVariable Integer documentoId) {
		return new ResponseEntity<Documento>(service.findByDocumentoId(documentoId), HttpStatus.OK);
	}

}
