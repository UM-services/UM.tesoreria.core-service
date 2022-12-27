/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Contrato;
import ar.edu.um.tesoreria.rest.service.ContratoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contrato")
public class ContratoController {

	@Autowired
	private ContratoService service;

	@GetMapping("/ajustable/{referencia}")
	public ResponseEntity<List<Contrato>> findAllAjustable(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime referencia) {
		return new ResponseEntity<List<Contrato>>(service.findAllAjustable(referencia), HttpStatus.OK);
	}

	@GetMapping("/vigente/{referencia}")
	public ResponseEntity<List<Contrato>> findAllVigente(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime referencia) {
		return new ResponseEntity<List<Contrato>>(service.findAllVigente(referencia), HttpStatus.OK);
	}

	@GetMapping("/persona/{personaId}/{documentoId}")
	public ResponseEntity<List<Contrato>> findAllByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<List<Contrato>>(service.findAllByPersona(personaId, documentoId), HttpStatus.OK);
	}

	@GetMapping("/contratado/{contratadoId}")
	public ResponseEntity<List<Contrato>> findAllByContratado(@PathVariable Long contratadoId) {
		return new ResponseEntity<List<Contrato>>(service.findAllByContratado(contratadoId), HttpStatus.OK);
	}

	@GetMapping("/{contratoId}")
	public ResponseEntity<Contrato> findByContratoId(@PathVariable Long contratoId) {
		return new ResponseEntity<Contrato>(service.findByContratoId(contratoId), HttpStatus.OK);
	}

	@PutMapping("/{contratoId}")
	public ResponseEntity<Contrato> update(@PathVariable Long contratoId, @RequestBody Contrato contrato) {
		return new ResponseEntity<Contrato>(service.update(contrato, contratoId), HttpStatus.OK);
	}

	@PutMapping("/")
	public ResponseEntity<List<Contrato>> saveAll(@RequestBody List<Contrato> contratos) {
		return new ResponseEntity<List<Contrato>>(service.saveAll(contratos), HttpStatus.OK);
	}

}
