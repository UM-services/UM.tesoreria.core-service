/**
 * 
 */
package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.PersonaSuspendido;
import um.tesoreria.core.service.facade.MailChequeraService;
import um.tesoreria.core.service.facade.NotificacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/notificacion")
@RequiredArgsConstructor
public class NotificacionController {

	private final NotificacionService service;

	private final MailChequeraService mailChequeraService;

	@GetMapping("/notifyDeudorChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<String> notifyDeudorChequera(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
		return ResponseEntity.accepted().body(
				mailChequeraService.notificaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId));
	}

	@GetMapping("/notaDeudorChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<String> notaDeudorChequera(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
		return ResponseEntity.ok(
				mailChequeraService.notaDeudorChequera(facultadId, tipoChequeraId, chequeraSerieId));
	}

	@PostMapping("/deudorSuspendido")
	public ResponseEntity<String> notifyDeudorSuspendido(@RequestBody PersonaSuspendido personaSuspendido) {
		return ResponseEntity.accepted().body(service.notifyDeudorSuspendido(personaSuspendido));
	}

	@GetMapping("/pagoProveedor/{proveedorMovimientoId}")
	public ResponseEntity<String> notifyPagoProveedor(@PathVariable Long proveedorMovimientoId) {
		return ResponseEntity.accepted().body(service.notifyPagoProveedor(proveedorMovimientoId));
	}

}
