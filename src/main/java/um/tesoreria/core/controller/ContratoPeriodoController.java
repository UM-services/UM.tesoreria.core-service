/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
public class ContratoPeriodoController {

	@Autowired
	private ContratoPeriodoService service;

	@GetMapping("/contrato/{contratoId}")
	public ResponseEntity<List<ContratoPeriodo>> findAllByContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<List<ContratoPeriodo>>(service.findAllByContrato(contratoId), HttpStatus.OK);
	}

	@GetMapping("/contratofactura/{contratofacturaId}")
	public ResponseEntity<List<ContratoPeriodo>> findAllByContratoFactura(@PathVariable Long contratofacturaId) {
		return new ResponseEntity<List<ContratoPeriodo>>(service.findAllByContratoFactura(contratofacturaId),
				HttpStatus.OK);
	}

	@GetMapping("/pendiente/{contratoId}")
	public ResponseEntity<List<ContratoPeriodo>> findAllPendienteByContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<List<ContratoPeriodo>>(service.findAllPendienteByContrato(contratoId), HttpStatus.OK);
	}

	@GetMapping("/periodo/{contratoId}/{anho}/{mes}")
	public ResponseEntity<ContratoPeriodo> findByPeriodo(@PathVariable Long contratoId, @PathVariable Integer anho,
			@PathVariable Integer mes) {
		return new ResponseEntity<ContratoPeriodo>(service.findByPeriodo(contratoId, anho, mes), HttpStatus.OK);
	}

	@GetMapping("/{contratoPeriodoId}")
	public ResponseEntity<ContratoPeriodo> findByContratoPeriodoId(@PathVariable Long contratoPeriodoId) {
		return new ResponseEntity<ContratoPeriodo>(service.findByContratoPeriodoId(contratoPeriodoId), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<ContratoPeriodo> add(@RequestBody ContratoPeriodo contratoperiodo) {
		return new ResponseEntity<ContratoPeriodo>(service.add(contratoperiodo), HttpStatus.OK);
	}

	@PutMapping("/{contratoperiodoId}")
	public ResponseEntity<ContratoPeriodo> update(@RequestBody ContratoPeriodo contratoperiodo,
			@PathVariable Long contratoperiodoId) {
		return new ResponseEntity<ContratoPeriodo>(service.update(contratoperiodo, contratoperiodoId), HttpStatus.OK);
	}

	@DeleteMapping("/{contratoPeriodoId}")
	public ResponseEntity<Void> deleteByContratoPeriodoId(@PathVariable Long contratoPeriodoId) {
		service.deleteByContratoPeriodoId(contratoPeriodoId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
