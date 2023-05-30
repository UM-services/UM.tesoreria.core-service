/**
 * 
 */
package um.tesoreria.rest.controller.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.rest.exception.view.OrdenPagoEntregadoException;
import um.tesoreria.rest.model.view.OrdenPagoEntregado;
import um.tesoreria.rest.service.view.OrdenPagoEntregadoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/ordenPagoEntregado")
public class OrdenPagoEntregadoController {
	
	@Autowired
	private OrdenPagoEntregadoService service;

	@GetMapping("/{ordenPagoId}")
	public ResponseEntity<OrdenPagoEntregado> findByOrdenPagoId(@PathVariable Long ordenPagoId) {
		try {
			return new ResponseEntity<OrdenPagoEntregado>(service.findByOrdenPagoId(ordenPagoId), HttpStatus.OK);
		} catch (OrdenPagoEntregadoException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}
}
