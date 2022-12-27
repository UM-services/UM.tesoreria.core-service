/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.view.CuentaMensual;
import ar.edu.um.tesoreria.rest.service.view.CuentaMensualService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cuentamensual")
public class CuentaMensualController {
	
	@Autowired
	private CuentaMensualService service;
	
	@GetMapping("/ingresos/{anho}/{mes}")
	public ResponseEntity<List<CuentaMensual>> findIngresosByMes(@PathVariable Integer anho, @PathVariable Integer mes) {
		return new ResponseEntity<List<CuentaMensual>>(service.findIngresosByMes(anho, mes), HttpStatus.OK);
	}

	@GetMapping("/gastos/{anho}/{mes}")
	public ResponseEntity<List<CuentaMensual>> findGastosByMes(@PathVariable Integer anho, @PathVariable Integer mes) {
		return new ResponseEntity<List<CuentaMensual>>(service.findGastosByMes(anho, mes), HttpStatus.OK);
	}
}
