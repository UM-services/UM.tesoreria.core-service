/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.ContratoPeriodo;
import um.tesoreria.core.service.ContratoPeriodoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratoperiodo")
@RequiredArgsConstructor
public class ContratoPeriodoController {

	private final ContratoPeriodoService service;

	@GetMapping("/contrato/{contratoId}")
	public ResponseEntity<List<ContratoPeriodo>> findAllByContrato(@PathVariable Long contratoId) {
		return ResponseEntity.ok(service.findAllByContrato(contratoId));
	}

	@GetMapping("/contratofactura/{contratofacturaId}")
	public ResponseEntity<List<ContratoPeriodo>> findAllByContratoFactura(@PathVariable Long contratofacturaId) {
		return ResponseEntity.ok(service.findAllByContratoFactura(contratofacturaId));
	}

	@GetMapping("/pendiente/{contratoId}")
	public ResponseEntity<List<ContratoPeriodo>> findAllPendienteByContrato(@PathVariable Long contratoId) {
		return ResponseEntity.ok(service.findAllPendienteByContrato(contratoId));
	}

	@GetMapping("/periodo/{contratoId}/{anho}/{mes}")
	public ResponseEntity<ContratoPeriodo> findByPeriodo(@PathVariable Long contratoId, @PathVariable Integer anho,
			@PathVariable Integer mes) {
		return ResponseEntity.ok(service.findByPeriodo(contratoId, anho, mes));
	}

	@GetMapping("/{contratoPeriodoId}")
	public ResponseEntity<ContratoPeriodo> findByContratoPeriodoId(@PathVariable Long contratoPeriodoId) {
		return ResponseEntity.ok(service.findByContratoPeriodoId(contratoPeriodoId));
	}

	@PostMapping("/")
	public ResponseEntity<ContratoPeriodo> add(@RequestBody ContratoPeriodo contratoperiodo) {
		return ResponseEntity.ok(service.add(contratoperiodo));
	}

	@PutMapping("/{contratoperiodoId}")
	public ResponseEntity<ContratoPeriodo> update(@RequestBody ContratoPeriodo contratoperiodo,
			@PathVariable Long contratoperiodoId) {
		return ResponseEntity.ok(service.update(contratoperiodo, contratoperiodoId));
	}

	@DeleteMapping("/{contratoPeriodoId}")
	public ResponseEntity<Void> deleteByContratoPeriodoId(@PathVariable Long contratoPeriodoId) {
		service.deleteByContratoPeriodoId(contratoPeriodoId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
