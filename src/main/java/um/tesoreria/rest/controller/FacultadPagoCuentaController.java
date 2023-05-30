/**
 * 
 */
package um.tesoreria.rest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.model.FacultadPagoCuenta;
import um.tesoreria.rest.service.FacultadPagoCuentaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/facultadpagocuenta")
public class FacultadPagoCuentaController {

	@Autowired
	private FacultadPagoCuentaService service;

	@GetMapping("/unique/{facultadId}/{tipoPagoId}")
	public ResponseEntity<FacultadPagoCuenta> findByUnique(@PathVariable Integer facultadId,
			@PathVariable Integer tipoPagoId) {
		return new ResponseEntity<FacultadPagoCuenta>(service.findByUnique(facultadId, tipoPagoId), HttpStatus.OK);
	}

}
