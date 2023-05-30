/**
 * 
 */
package um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.model.CursoCargoContratado;
import um.tesoreria.rest.service.CursoCargoContratadoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/cursocargocontratado")
public class CursoCargoContratadoController {

	@Autowired
	private CursoCargoContratadoService service;

	@GetMapping("/cursos/{contratadoId}/{anho}/{mes}/{contratoId}")
	public ResponseEntity<List<CursoCargoContratado>> findAllByContratado(@PathVariable Long contratadoId,
			@PathVariable Integer anho, @PathVariable Integer mes, @PathVariable Long contratoId) {
		return new ResponseEntity<List<CursoCargoContratado>>(
				service.findAllByContratado(contratadoId, anho, mes, contratoId), HttpStatus.OK);
	}

	@GetMapping("/curso/{cursoId}/{anho}/{mes}")
	public ResponseEntity<List<CursoCargoContratado>> findAllByCurso(@PathVariable Long cursoId,
			@PathVariable Integer anho, @PathVariable Integer mes) {
		return new ResponseEntity<List<CursoCargoContratado>>(service.findAllByCurso(cursoId, anho, mes),
				HttpStatus.OK);
	}

	@GetMapping("/{cursocargocontratadoId}")
	public ResponseEntity<CursoCargoContratado> findByCursoCargo(@PathVariable Long cursocargocontratadoId) {
		return new ResponseEntity<CursoCargoContratado>(service.findByCursoCargo(cursocargocontratadoId),
				HttpStatus.OK);
	}

	@GetMapping("/contratado/{cursoId}/{anho}/{mes}/{contratadoId}")
	public ResponseEntity<CursoCargoContratado> findByContratado(@PathVariable Long cursoId, @PathVariable Integer anho,
			@PathVariable Integer mes, @PathVariable Long contratadoId) {
		return new ResponseEntity<CursoCargoContratado>(service.findByContratado(cursoId, anho, mes, contratadoId),
				HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<CursoCargoContratado> add(@RequestBody CursoCargoContratado cursocargocontratado) {
		return new ResponseEntity<CursoCargoContratado>(service.add(cursocargocontratado), HttpStatus.OK);
	}

	@PutMapping("/{cursocargocontratadoId}")
	public ResponseEntity<CursoCargoContratado> update(@RequestBody CursoCargoContratado cursocargocontratado,
			@PathVariable Long cursocargocontratadoId) {
		return new ResponseEntity<CursoCargoContratado>(service.update(cursocargocontratado, cursocargocontratadoId),
				HttpStatus.OK);
	}

	@DeleteMapping("/{cursocargocontratadoId}")
	public ResponseEntity<Void> delete(@PathVariable Long cursocargocontratadoId) {
		service.delete(cursocargocontratadoId);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
