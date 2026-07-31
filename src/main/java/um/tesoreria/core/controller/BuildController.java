/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.kotlin.model.Build;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.BuildService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/build")
@RequiredArgsConstructor
public class BuildController {

	private final BuildService service;

	@GetMapping("/last")
	public ResponseEntity<Build> findLast() {
		return ResponseEntity.ok(service.findLast());
	}

	@PostMapping("/")
	public ResponseEntity<Build> add() {
		return ResponseEntity.ok(service.add(new Build()));
	}

}
