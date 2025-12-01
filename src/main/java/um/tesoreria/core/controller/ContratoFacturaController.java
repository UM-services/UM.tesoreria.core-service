/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import lombok.RequiredArgsConstructor;
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

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ContratoFacturaException;
import um.tesoreria.core.model.ContratoFactura;
import um.tesoreria.core.service.ContratoFacturaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratofactura")
@RequiredArgsConstructor
public class ContratoFacturaController {

	private final ContratoFacturaService service;

	@GetMapping("/pendientefacultad/{facultadId}/{geograficaId}")
	public ResponseEntity<List<ContratoFactura>> findAllPendienteByFacultad(@PathVariable Integer facultadId,
			@PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllPendienteByFacultad(facultadId, geograficaId));
	}

	@GetMapping("/pendientepersona/{personaId}/{documentoId}")
	public ResponseEntity<List<ContratoFactura>> findAllPendienteByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
        return ResponseEntity.ok(service.findAllPendienteByPersona(personaId, documentoId));
	}

	@GetMapping("/excluidopersona/{personaId}/{documentoId}")
	public ResponseEntity<List<ContratoFactura>> findAllExcluidoByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
        return ResponseEntity.ok(service.findAllExcluidoByPersona(personaId, documentoId));
	}

	@GetMapping("/contrato/{contratoId}")
	public ResponseEntity<List<ContratoFactura>> findAllByContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(service.findAllByContrato(contratoId));
	}

	@GetMapping("/{contratofacturaId}")
	public ResponseEntity<ContratoFactura> findByContratofacturaId(@PathVariable Long contratofacturaId) {
        try {
            return ResponseEntity.ok(service.findByContratofacturaId(contratofacturaId));
        } catch (ContratoFacturaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

	@GetMapping("/persona/{personaId}/{documentoId}")
	public ResponseEntity<ContratoFactura> findLastByPersona(@PathVariable BigDecimal personaId,
			@PathVariable Integer documentoId) {
        try {
            return ResponseEntity.ok(service.findLastByPersona(personaId, documentoId));
        } catch (ContratoFacturaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
	}

	@PostMapping("/")
	public ResponseEntity<ContratoFactura> add(@RequestBody ContratoFactura contratofactura) {
        return ResponseEntity.ok(service.add(contratofactura));
	}

	@PutMapping("/{contratofacturaId}")
	public ResponseEntity<ContratoFactura> update(@RequestBody ContratoFactura contratofactura,
			@PathVariable Long contratofacturaId) {
        return ResponseEntity.ok(service.update(contratofactura, contratofacturaId));
	}

}
