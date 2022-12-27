/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.EntregaNotFoundException;
import ar.edu.um.tesoreria.rest.model.CuentaMovimiento;
import ar.edu.um.tesoreria.rest.model.view.CuentaMovimientoAsiento;
import ar.edu.um.tesoreria.rest.service.CuentaMovimientoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cuentaMovimiento")
public class CuentaMovimientoController {

	@Autowired
	private CuentaMovimientoService service;

	@GetMapping("/entregaDetalle/{proveedorMovimientoId}")
	public ResponseEntity<List<CuentaMovimientoAsiento>> findAllEntregaDetalleByProveedorMovimientoId(
			@PathVariable Long proveedorMovimientoId) {
		return new ResponseEntity<List<CuentaMovimientoAsiento>>(
				service.findAllEntregaDetalleByProveedorMovimientoId(proveedorMovimientoId), HttpStatus.OK);
	}

	@GetMapping("/{cuentaMovimientoId}")
	public ResponseEntity<CuentaMovimiento> findByCuentaMovimientoId(@PathVariable Long cuentaMovimientoId) {
		try {
			return new ResponseEntity<CuentaMovimiento>(service.findByCuentaMovimientoId(cuentaMovimientoId),
					HttpStatus.OK);
		} catch (EntregaNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
