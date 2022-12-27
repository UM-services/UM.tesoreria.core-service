/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.Contrato;
import ar.edu.um.tesoreria.rest.model.ContratoFactura;
import ar.edu.um.tesoreria.rest.service.facade.ContratoToolService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratotool")
public class ContratoToolController {

	@Autowired
	private ContratoToolService service;

	@PostMapping("/addfactura")
	public ResponseEntity<Boolean> addFactura(@RequestBody ContratoFactura contratofactura) {
		return new ResponseEntity<Boolean>(service.addFactura(contratofactura), HttpStatus.OK);
	}

	@DeleteMapping("/deletefactura/{contratofacturaId}")
	public ResponseEntity<Boolean> deleteFactura(@PathVariable Long contratofacturaId) {
		return new ResponseEntity<Boolean>(service.deleteFactura(contratofacturaId), HttpStatus.OK);
	}

	@DeleteMapping("/deletecontrato/{contratoId}")
	public ResponseEntity<Boolean> deleteContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<Boolean>(service.deleteContrato(contratoId), HttpStatus.OK);
	}

	@GetMapping("/depuracontrato/{contratoId}")
	public ResponseEntity<Boolean> depuraContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<Boolean>(service.depuraContrato(contratoId), HttpStatus.OK);
	}

	@PostMapping("/savecontrato")
	public ResponseEntity<Boolean> saveContrato(@RequestBody Contrato contrato) {
		return new ResponseEntity<Boolean>(service.saveContrato(contrato), HttpStatus.OK);
	}

	@GetMapping("/anulaenvio/{fecha}/{envio}")
	public ResponseEntity<Boolean> anulaEnvio(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fecha,
			@PathVariable Integer envio) {
		return new ResponseEntity<Boolean>(service.anulaEnvio(fecha, envio), HttpStatus.OK);
	}

	@GetMapping("/savecurso/{cursoId}/{contratoId}/{cargotipoId}/{horasSemanales}")
	public ResponseEntity<Boolean> saveCurso(@PathVariable Long cursoId, @PathVariable Long contratoId,
			@PathVariable Integer cargotipoId, @PathVariable BigDecimal horasSemanales) {
		return new ResponseEntity<Boolean>(service.saveCurso(cursoId, contratoId, cargotipoId, horasSemanales),
				HttpStatus.OK);
	}

	@DeleteMapping("deleteCurso/{cursoCargoContratadoId}")
	public ResponseEntity<Void> deleteCurso(@PathVariable Long cursoCargoContratadoId) {
		service.deleteCurso(cursoCargoContratadoId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	// Se agrega sólo para arreglar la consistencia de las asignaciones
	@GetMapping("/generatecurso/{anho}/{mes}")
	public ResponseEntity<Void> generateCurso(@PathVariable Integer anho, @PathVariable Integer mes) {
		service.generateCurso(anho, mes);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

}
