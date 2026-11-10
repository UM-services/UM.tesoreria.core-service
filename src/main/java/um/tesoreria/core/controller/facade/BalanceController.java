/**
 *
 */
package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.facade.BalanceService;
import um.tesoreria.core.service.facade.mayor.MayorConsultaRequest;
import um.tesoreria.core.service.facade.mayor.MayorConsultaResponse;
import um.tesoreria.core.service.facade.mayor.MayorConsultaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/balance", "/api/tesoreria/core/balance"})
@RequiredArgsConstructor
public class BalanceController {

	private final BalanceService service;
	private final MayorConsultaService mayorConsultaService;


	@GetMapping("/sumasysaldos/{desde}/{hasta}")
	public ResponseEntity<Resource> makeSumasSaldos2(
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta) throws FileNotFoundException {
		String filename = service.makeSumasSaldos(desde, hasta);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sumas_y_saldos.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	/** Mayor en JSON de una o varias cuentas, con rango de fechas que puede cruzar ejercicios. */
	@PostMapping("/mayor/consulta")
	public ResponseEntity<MayorConsultaResponse> consultarMayor(@RequestBody MayorConsultaRequest request) {
		return ResponseEntity.ok(mayorConsultaService.consultar(request));
	}

	@GetMapping("/mayor/{numeroCuenta}/{desde}/{hasta}")
	public ResponseEntity<Resource> makeMayor(@PathVariable BigDecimal numeroCuenta,
											  @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
											  @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta) throws FileNotFoundException {
		String filename = service.makeMayor(numeroCuenta, desde, hasta);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=mayor.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

}