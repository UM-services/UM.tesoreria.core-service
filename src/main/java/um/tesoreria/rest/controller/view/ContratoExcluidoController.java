/**
 * 
 */
package um.tesoreria.rest.controller.view;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.model.view.ContratoExcluido;
import um.tesoreria.rest.service.view.ContratoExcluidoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/contratoexcluido")
public class ContratoExcluidoController {

	@Autowired
	private ContratoExcluidoService service;

	@GetMapping("/")
	public ResponseEntity<List<ContratoExcluido>> findAll() {
		return new ResponseEntity<List<ContratoExcluido>>(service.findAll(), HttpStatus.OK);
	}
}
