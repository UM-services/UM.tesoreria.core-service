/**
 * 
 */
package um.tesoreria.core.controller.dto;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CostoParameterController {

	private final CostoParameterService service;

	@GetMapping("/")
	public ResponseEntity<CostoParameterDto> findParameters() {
		return ResponseEntity.ok(service.findParameters());
	}

}
