/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.facade.ToolService;
import lombok.extern.slf4j.Slf4j;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/tool", "/api/tesoreria/core/tool"})
@Slf4j
public class ToolController {

	private final ToolService service;

	public ToolController(ToolService service) {
		this.service = service;
	}

	@PostMapping("/mailvalidate")
	public ResponseEntity<Boolean> mailValidate(@RequestBody List<String> mailes) {
		log.debug(mailes.toString());
		return ResponseEntity.ok(service.mailValidate(mailes.getFirst()));
	}

}
