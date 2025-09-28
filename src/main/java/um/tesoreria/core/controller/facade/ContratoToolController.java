/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ContratoToolController {

	private final ContratoToolService service;

	@PostMapping("/addfactura")
	public ResponseEntity<Boolean> addFactura(@RequestBody ContratoFactura contratofactura) {
        return ResponseEntity.ok(service.addFactura(contratofactura));
	}

	@DeleteMapping("/deletefactura/{contratofacturaId}")
	public ResponseEntity<Boolean> deleteFactura(@PathVariable Long contratofacturaId) {
        return ResponseEntity.ok(service.deleteFactura(contratofacturaId));
	}

	@DeleteMapping("/deletecontrato/{contratoId}")
	public ResponseEntity<Boolean> deleteContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(service.deleteContrato(contratoId));
	}

	@GetMapping("/depuracontrato/{contratoId}")
	public ResponseEntity<Boolean> depuraContrato(@PathVariable Long contratoId) {
        return ResponseEntity.ok(service.depuraContrato(contratoId));
	}

	@PostMapping("/savecontrato")
	public ResponseEntity<Boolean> saveContrato(@RequestBody Contrato contrato) {
        return ResponseEntity.ok(service.saveContrato(contrato));
	}

	@GetMapping("/anulaenvio/{fecha}/{envio}")
	public ResponseEntity<Boolean> anulaEnvio(@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fecha,
			@PathVariable Integer envio) {
        return ResponseEntity.ok(service.anulaEnvio(fecha, envio));
	}

	@GetMapping("/savecurso/{cursoId}/{contratoId}/{cargotipoId}/{horasSemanales}")
	public ResponseEntity<Boolean> saveCurso(@PathVariable Long cursoId,
                                             @PathVariable Long contratoId,
                                             @PathVariable Integer cargotipoId,
                                             @PathVariable BigDecimal horasSemanales) {
        return ResponseEntity.ok(service.saveCurso(cursoId, contratoId, cargotipoId, horasSemanales));
	}

	@DeleteMapping("deleteCurso/{cursoCargoContratadoId}")
	public ResponseEntity<Void> deleteCurso(@PathVariable Long cursoCargoContratadoId) {
		service.deleteCurso(cursoCargoContratadoId);
        return ResponseEntity.noContent().build();
	}

	// Se agrega sólo para arreglar la consistencia de las asignaciones
	@GetMapping("/generatecurso/{anho}/{mes}")
	public ResponseEntity<Void> generateCurso(@PathVariable Integer anho, @PathVariable Integer mes) {
		service.generateCurso(anho, mes);
        return ResponseEntity.noContent().build();
	}

}
