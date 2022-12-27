/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.ContratoFactura;
import ar.edu.um.tesoreria.rest.service.ContratoFacturaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratofactura")
public class ContratoFacturaController {

	@Autowired
	private ContratoFacturaService service;

	@GetMapping("/pendientefacultad/{facultadId}/{geograficaId}")
	public ResponseEntity<List<ContratoFactura>> findAllPendienteByFacultad(@PathVariable Integer facultadId,
			@PathVariable Integer geograficaId) {
		return new ResponseEntity<List<ContratoFactura>>(service.findAllPendienteByFacultad(facultadId, geograficaId),
				HttpStatus.OK);
	}

	@GetMapping("/pendientepersona/{personaId}/{documentoId}")
	public ResponseEntity<List<ContratoFactura>> findAllPendienteByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<List<ContratoFactura>>(service.findAllPendienteByPersona(personaId, documentoId),
				HttpStatus.OK);
	}

	@GetMapping("/excluidopersona/{personaId}/{documentoId}")
	public ResponseEntity<List<ContratoFactura>> findAllExcluidoByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<List<ContratoFactura>>(service.findAllExcluidoByPersona(personaId, documentoId),
				HttpStatus.OK);
	}

	@GetMapping("/contrato/{contratoId}")
	public ResponseEntity<List<ContratoFactura>> findAllByContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<List<ContratoFactura>>(service.findAllByContrato(contratoId), HttpStatus.OK);
	}

	@GetMapping("/{contratofacturaId}")
	public ResponseEntity<ContratoFactura> findByContratofacturaId(@PathVariable Long contratofacturaId) {
		return new ResponseEntity<ContratoFactura>(service.findByContratofacturaId(contratofacturaId), HttpStatus.OK);
	}

	@GetMapping("/persona/{personaId}/{documentoId}")
	public ResponseEntity<ContratoFactura> findLastByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<ContratoFactura>(service.findLastByPersona(personaId, documentoId), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<ContratoFactura> add(@RequestBody ContratoFactura contratofactura) {
		return new ResponseEntity<ContratoFactura>(service.add(contratofactura), HttpStatus.OK);
	}

	@PutMapping("/{contratofacturaId}")
	public ResponseEntity<ContratoFactura> update(@RequestBody ContratoFactura contratofactura,
			@PathVariable Long contratofacturaId) {
		return new ResponseEntity<ContratoFactura>(service.update(contratofactura, contratofacturaId), HttpStatus.OK);
	}

}
