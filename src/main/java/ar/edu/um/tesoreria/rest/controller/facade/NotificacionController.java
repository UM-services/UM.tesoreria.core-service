/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.facade;

import java.math.BigDecimal;

import jakarta.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.PersonaSuspendido;
import ar.edu.um.tesoreria.rest.service.facade.MailChequeraService;
import ar.edu.um.tesoreria.rest.service.facade.NotificacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/notificacion")
public class NotificacionController {

	@Autowired
	private NotificacionService service;

	@Autowired
	private MailChequeraService mailChequeraService;

	@GetMapping("/48horas/{personaId}/{documentoId}/{facultadId}")
	public ResponseEntity<Void> notifyByPersona(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId,
			@PathVariable Integer facultadId) {
		service.notifyByPersona(personaId, documentoId, facultadId);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@GetMapping("/notifyDeudorChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<String> notifyDeudorChequera(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) throws MessagingException {
		return new ResponseEntity<String>(
				mailChequeraService.notificaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId), HttpStatus.OK);
	}

	@GetMapping("/notaDeudorChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<String> notaDeudorChequera(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) throws MessagingException {
		return new ResponseEntity<String>(
				mailChequeraService.notaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId), HttpStatus.OK);
	}

	@PostMapping("/deudorSuspendido")
	public ResponseEntity<String> notifyDeudorSuspendido(@RequestBody PersonaSuspendido personaSuspendido)
			throws MessagingException {
		return new ResponseEntity<String>(service.notifyDeudorSuspendido(personaSuspendido), HttpStatus.OK);
	}

	@GetMapping("/pagoProveedor/{proveedorMovimientoId}")
	public ResponseEntity<String> notifyPagoProveedor(@PathVariable Long proveedorMovimientoId) throws MessagingException {
		return new ResponseEntity<String>(service.notifyPagoProveedor(proveedorMovimientoId), HttpStatus.OK);
	}

}
