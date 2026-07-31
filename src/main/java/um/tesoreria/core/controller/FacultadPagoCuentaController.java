/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.FacultadPagoCuenta;
import um.tesoreria.core.service.FacultadPagoCuentaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/facultadpagocuenta")
@RequiredArgsConstructor
public class FacultadPagoCuentaController {

	private final FacultadPagoCuentaService service;

	@GetMapping("/unique/{facultadId}/{tipoPagoId}")
	public ResponseEntity<FacultadPagoCuenta> findByUnique(@PathVariable Integer facultadId,
			@PathVariable Integer tipoPagoId) {
		return ResponseEntity.ok(service.findByUnique(facultadId, tipoPagoId));
	}

}
