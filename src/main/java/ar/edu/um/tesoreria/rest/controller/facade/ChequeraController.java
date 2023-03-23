/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;

import ar.edu.um.tesoreria.rest.kotlin.model.dto.PreuniversitarioData;
import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.SpoterData;
import ar.edu.um.tesoreria.rest.model.dto.SpoterDataResponse;
import ar.edu.um.tesoreria.rest.service.facade.ChequeraService;
import ar.edu.um.tesoreria.rest.service.facade.FormulariosToPdfService;
import ar.edu.um.tesoreria.rest.service.facade.MailChequeraService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequera")
public class ChequeraController {

	@Autowired
	private ChequeraService service;

	@Autowired
	private MailChequeraService mailChequeraService;

	@Autowired
	private FormulariosToPdfService formularioToPdfService;

	@DeleteMapping("/delete/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{usuarioId}")
	public ResponseEntity<Void> deleteByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
			@PathVariable Long chequeraSerieId, @PathVariable String usuarioId) {
		service.deleteByChequera(facultadId, tipoChequeraId, chequeraSerieId, usuarioId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/sendChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{copiaInformes}")
	public ResponseEntity<String> sendChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
			@PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId,
			@PathVariable Boolean copiaInformes) throws MessagingException {
		return new ResponseEntity<String>(mailChequeraService.sendChequera(facultadId, tipoChequeraId, chequeraSerieId,
				alternativaId, copiaInformes, true), HttpStatus.OK);
	}

	@GetMapping("/notificaDeudor/{personaId}/{documentoId}")
	public String notificaDeudor(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId)
			throws MessagingException {
		return mailChequeraService.notificaDeudor(personaId, documentoId);
	}

	@GetMapping("/generatePdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
	public ResponseEntity<Resource> generatePdf(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
			@PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId) throws FileNotFoundException {
		String filename = formularioToPdfService.generateChequeraPdf(facultadId, tipoChequeraId, chequeraSerieId,
				alternativaId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=chequera.pdf");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/extendDebito/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<Void> extendDebito(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
			@PathVariable Long chequeraSerieId) {
		service.extendDebito(facultadId, tipoChequeraId, chequeraSerieId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/track/{chequeraId}")
	public ResponseEntity<Void> track(@PathVariable Long chequeraId) {
		service.track(chequeraId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@PostMapping("/spoter")
	public ResponseEntity<SpoterDataResponse> sendChequeraPreSpoter(@RequestBody SpoterData spoterData)
			throws MessagingException {
		try {
			return new ResponseEntity<SpoterDataResponse>(mailChequeraService.sendChequeraPreSpoter(spoterData),
					HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<SpoterDataResponse>(
					new SpoterDataResponse(false, e.getMessage(), null, null, null), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/lastPre/{facultadId}/{personaId}/{documentoId}")
	public ResponseEntity<PreuniversitarioData> lastPreData(@PathVariable Integer facultadId,
															@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
		return new ResponseEntity<PreuniversitarioData>(service.findLastPreData(facultadId, personaId, documentoId),
				HttpStatus.OK);
	}

}
