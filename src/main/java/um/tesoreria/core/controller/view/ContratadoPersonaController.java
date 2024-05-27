/**
 * 
 */
package um.tesoreria.core.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.view.ContratadoPersona;
import um.tesoreria.core.service.view.ContratadoPersonaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratadopersona")
public class ContratadoPersonaController {

	@Autowired
	private ContratadoPersonaService service;

	@GetMapping("/")
	public ResponseEntity<List<ContratadoPersona>> findAll() {
		return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
	}
}
