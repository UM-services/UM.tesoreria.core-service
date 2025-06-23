/**
 * 
 */
package um.tesoreria.core.controller.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.dto.CostoParameterDto;
import um.tesoreria.core.service.dto.CostoParameterService;

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
	public ResponseEntity<CostoParameterDto> findParameters() {
		return new ResponseEntity<CostoParameterDto>(service.findParameters(), HttpStatus.OK);
	}

}
