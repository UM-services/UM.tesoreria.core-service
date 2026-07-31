/**
 * 
 */
package um.tesoreria.core.controller.view;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.view.CuentaMensual;
import um.tesoreria.core.service.view.CuentaMensualService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cuentamensual")
@RequiredArgsConstructor
public class CuentaMensualController {
	
	private final CuentaMensualService service;
	
	@GetMapping("/ingresos/{anho}/{mes}")
	public ResponseEntity<List<CuentaMensual>> findIngresosByMes(@PathVariable Integer anho, @PathVariable Integer mes) {
		return ResponseEntity.ok(service.findIngresosByMes(anho, mes));
	}

	@GetMapping("/gastos/{anho}/{mes}")
	public ResponseEntity<List<CuentaMensual>> findGastosByMes(@PathVariable Integer anho, @PathVariable Integer mes) {
		return ResponseEntity.ok(service.findGastosByMes(anho, mes));
	}
}
