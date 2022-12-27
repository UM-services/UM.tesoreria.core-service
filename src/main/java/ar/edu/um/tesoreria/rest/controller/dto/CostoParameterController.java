/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.dto.CostoParameter;
import ar.edu.um.tesoreria.rest.service.dto.CostoParameterService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/costoParameter")
public class CostoParameterController {

	@Autowired
	private CostoParameterService service;

	@GetMapping("/")
	public ResponseEntity<CostoParameter> findParameters() {
		return new ResponseEntity<CostoParameter>(service.findParameters(), HttpStatus.OK);
	}

}
