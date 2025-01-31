/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.dto.TipoPagoFechaDto;
import um.tesoreria.core.service.facade.TipoPagoFechaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/tipopagofecha", "/api/tesoreria/core/tipopagofecha"})
public class TipoPagoFechaController {

	private final TipoPagoFechaService service;

	public TipoPagoFechaController(TipoPagoFechaService service) {
		this.service = service;
	}

	@GetMapping("/fecha/{fecha}")
	public ResponseEntity<List<TipoPagoFechaDto>> findAllByFecha(
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fecha) {
		return new ResponseEntity<>(service.findAllByFecha(fecha), HttpStatus.OK);
	}

}
