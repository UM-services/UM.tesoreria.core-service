/**
 * 
 */
package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.kotlin.model.CargoMateria;
import um.tesoreria.core.service.CargoMateriaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cargomateria")
@RequiredArgsConstructor
public class CargoMateriaController {

	private final CargoMateriaService service;

	@GetMapping("/")
	public ResponseEntity<List<CargoMateria>> findAll() {
		return ResponseEntity.ok(service.findAll());
	}

}
