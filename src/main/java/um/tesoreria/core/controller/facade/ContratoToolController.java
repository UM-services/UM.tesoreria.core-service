/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

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

import um.tesoreria.core.model.Contrato;
import um.tesoreria.core.model.ContratoFactura;
import um.tesoreria.core.service.facade.ContratoToolService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/contratotool", "/api/core/contratotool"})
public class ContratoToolController {

	private final ContratoToolService service;

	public ContratoToolController(ContratoToolService service) {
		this.service = service;
	}

	@PostMapping("/addfactura")
	public ResponseEntity<Boolean> addFactura(@RequestBody ContratoFactura contratofactura) {
		return new ResponseEntity<>(service.addFactura(contratofactura), HttpStatus.OK);
	}

	@DeleteMapping("/deletefactura/{contratofacturaId}")
	public ResponseEntity<Boolean> deleteFactura(@PathVariable Long contratofacturaId) {
		return new ResponseEntity<>(service.deleteFactura(contratofacturaId), HttpStatus.OK);
	}

	@DeleteMapping("/deletecontrato/{contratoId}")
	public ResponseEntity<Boolean> deleteContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<>(service.deleteContrato(contratoId), HttpStatus.OK);
	}

	@GetMapping("/depuracontrato/{contratoId}")
	public ResponseEntity<Boolean> depuraContrato(@PathVariable Long contratoId) {
		return new ResponseEntity<>(service.depuraContrato(contratoId), HttpStatus.OK);
	}

	@PostMapping("/savecontrato")
	public ResponseEntity<Boolean> saveContrato(@RequestBody Contrato contrato) {
		return new ResponseEntity<>(service.saveContrato(contrato), HttpStatus.OK);
	}

	@GetMapping("/anulaenvio/{fecha}/{envio}")
	public ResponseEntity<Boolean> anulaEnvio(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fecha,
			@PathVariable Integer envio) {
		return new ResponseEntity<>(service.anulaEnvio(fecha, envio), HttpStatus.OK);
	}

	@GetMapping("/savecurso/{cursoId}/{contratoId}/{cargotipoId}/{horasSemanales}")
	public ResponseEntity<Boolean> saveCurso(@PathVariable Long cursoId, @PathVariable Long contratoId,
			@PathVariable Integer cargotipoId, @PathVariable BigDecimal horasSemanales) {
		return new ResponseEntity<>(service.saveCurso(cursoId, contratoId, cargotipoId, horasSemanales),
				HttpStatus.OK);
	}

	@DeleteMapping("deleteCurso/{cursoCargoContratadoId}")
	public ResponseEntity<Void> deleteCurso(@PathVariable Long cursoCargoContratadoId) {
		service.deleteCurso(cursoCargoContratadoId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	// Se agrega sólo para arreglar la consistencia de las asignaciones
	@GetMapping("/generatecurso/{anho}/{mes}")
	public ResponseEntity<Void> generateCurso(@PathVariable Integer anho, @PathVariable Integer mes) {
		service.generateCurso(anho, mes);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
