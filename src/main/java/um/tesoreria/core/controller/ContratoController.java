/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.service.ContratoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contrato")
@RequiredArgsConstructor
public class ContratoController {

	private final ContratoService service;

	@GetMapping("/ajustable/{referencia}")
	public ResponseEntity<List<Contrato>> findAllAjustable(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime referencia) {
		return new ResponseEntity<>(service.findAllAjustable(referencia), HttpStatus.OK);
	}

	@GetMapping("/vigente/{referencia}")
	public ResponseEntity<List<Contrato>> findAllVigente(
			@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime referencia) {
		return new ResponseEntity<>(service.findAllVigente(referencia), HttpStatus.OK);
	}

	@GetMapping("/persona/{personaId}/{documentoId}")
	public ResponseEntity<List<Contrato>> findAllByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
		return new ResponseEntity<>(service.findAllByPersona(personaId, documentoId), HttpStatus.OK);
	}

	@GetMapping("/{contratoId}")
	public ResponseEntity<Contrato> findByContratoId(@PathVariable Long contratoId) {
		return new ResponseEntity<>(service.findByContratoId(contratoId), HttpStatus.OK);
	}

	@PutMapping("/{contratoId}")
	public ResponseEntity<Contrato> update(@PathVariable Long contratoId, @RequestBody Contrato contrato) {
		return new ResponseEntity<>(service.update(contrato, contratoId), HttpStatus.OK);
	}

	@PutMapping("/")
	public ResponseEntity<List<Contrato>> saveAll(@RequestBody List<Contrato> contratos) {
		return new ResponseEntity<>(service.saveAll(contratos), HttpStatus.OK);
	}

}
