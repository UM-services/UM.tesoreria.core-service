/**
 * 
 */
package um.tesoreria.core.controller.view;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.TipoPagoFecha;
import um.tesoreria.core.service.view.TipoPagoFechaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/tipopagofecha")
public class TipoPagoFechaController {

	@Autowired
	private TipoPagoFechaService service;

	@GetMapping("/fecha/{fechaAcreditacion}")
	public ResponseEntity<List<TipoPagoFecha>> findAllByFecha(
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fechaAcreditacion) {
		return new ResponseEntity<List<TipoPagoFecha>>(service.findAllByFechaAcreditacion(fechaAcreditacion), HttpStatus.OK);
	}
}
