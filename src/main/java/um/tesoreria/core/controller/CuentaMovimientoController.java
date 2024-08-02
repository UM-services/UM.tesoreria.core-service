/**
 * 
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import um.tesoreria.core.exception.EntregaException;
import um.tesoreria.core.kotlin.model.CuentaMovimiento;
import um.tesoreria.core.model.view.CuentaMovimientoAsiento;
import um.tesoreria.core.service.CuentaMovimientoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/cuentaMovimiento", "/api/tesoreria/core/cuentaMovimiento"})
public class CuentaMovimientoController {

	private final CuentaMovimientoService service;

	public CuentaMovimientoController(CuentaMovimientoService service) {
		this.service = service;
	}

	@GetMapping("/cuenta/{numeroCuenta}/{onlyOne}")
	public ResponseEntity<List<CuentaMovimiento>> findAllByNumeroCuenta(@PathVariable BigDecimal numeroCuenta, @PathVariable Boolean onlyOne) {
		return new ResponseEntity<>(service.findAllByNumeroCuenta(numeroCuenta, onlyOne), HttpStatus.OK);
	}

	@GetMapping("/asiento/{fechaContable}/{ordenContable}")
	public ResponseEntity<List<CuentaMovimiento>> findAllByAsiento(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaContable, @PathVariable Integer ordenContable) {
		return new ResponseEntity<>(service.findAllByAsiento(fechaContable, ordenContable, 0, 2), HttpStatus.OK);
	}

	@GetMapping("/entregaDetalle/{proveedorMovimientoId}")
	public ResponseEntity<List<CuentaMovimientoAsiento>> findAllEntregaDetalleByProveedorMovimientoId(
			@PathVariable Long proveedorMovimientoId) throws JsonProcessingException {
		return new ResponseEntity<>(
				service.findAllEntregaDetalleByProveedorMovimientoId(proveedorMovimientoId), HttpStatus.OK);
	}

	@PostMapping("/entregaDetalle")
	public ResponseEntity<List<CuentaMovimientoAsiento>> findAllEntregaDetalleByProveedorMovimientoIds(
			@RequestBody List<Long> proveedorMovimientoIds) throws JsonProcessingException {
		return new ResponseEntity<>(
				service.findAllEntregaDetalleByProveedorMovimientoIds(proveedorMovimientoIds), HttpStatus.OK);
	}

	@GetMapping("/{cuentaMovimientoId}")
	public ResponseEntity<CuentaMovimiento> findByCuentaMovimientoId(@PathVariable Long cuentaMovimientoId) {
		try {
			return new ResponseEntity<>(service.findByCuentaMovimientoId(cuentaMovimientoId),
					HttpStatus.OK);
		} catch (EntregaException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

}
