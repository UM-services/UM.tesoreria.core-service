/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.CoeficienteInflacion;
import um.tesoreria.core.service.CoeficienteInflacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/coeficienteinflacion")
@RequiredArgsConstructor
public class CoeficienteInflacionController {

	private final CoeficienteInflacionService service;

	@GetMapping("/")
	public ResponseEntity<List<CoeficienteInflacion>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

	@GetMapping("/unique/{anho}/{mes}")
	public ResponseEntity<CoeficienteInflacion> findByUnique(@PathVariable Integer anho, @PathVariable Integer mes) {
		return ResponseEntity.ok(service.findByUnique(anho, mes));
	}

	@PostMapping("/")
	public ResponseEntity<CoeficienteInflacion> add(@RequestBody CoeficienteInflacion coeficienteinflacion) {
		return ResponseEntity.ok(service.add(coeficienteinflacion));
	}

	@PutMapping("/{coeficienteinflacionId}")
	public ResponseEntity<CoeficienteInflacion> update(@RequestBody CoeficienteInflacion coeficienteinflacion,
			@PathVariable Long coeficienteinflacionId) {
		return ResponseEntity.ok(service.update(coeficienteinflacion, coeficienteinflacionId));
	}

}
