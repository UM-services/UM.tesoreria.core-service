/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.service.facade.SheetService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/sheet")
public class SheetController {

	@Autowired
	private SheetService service;

	@GetMapping("/generateingresos/{anho}/{mes}")
	public ResponseEntity<Resource> generateIngresos(@PathVariable Integer anho, @PathVariable Integer mes)
			throws FileNotFoundException {
		String filename = service.generateIngresos(anho, mes);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingresos.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@PostMapping("/generatedeuda/{facultadId}/{lectivoId}/{soloDeudores}")
	public ResponseEntity<Resource> generateDeuda(@PathVariable Integer facultadId, @PathVariable Integer lectivoId,
			@PathVariable Boolean soloDeudores, @RequestBody List<Integer> tipochequeraIds)
			throws FileNotFoundException {
		String filename = service.generateDeuda(facultadId, lectivoId, soloDeudores, tipochequeraIds);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=deuda.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generateejercicioop/{ejercicioId}")
	public ResponseEntity<Resource> generateEjercicioOp(@PathVariable Integer ejercicioId)
			throws FileNotFoundException {
		String filename = service.generateEjercicioOp(ejercicioId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=listaop.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generateeficienciapre/{facultadId}/{lectivoId}")
	public ResponseEntity<Resource> generateEficienciaPre(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId) throws FileNotFoundException {
		String filename = service.generateEficienciaPre(facultadId, lectivoId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=eficienciapre.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generatecomparativopre/{facultadId}/{lectivoId}")
	public ResponseEntity<Resource> generateComparativoPre(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId) throws FileNotFoundException {
		String filename = service.generateComparativoPre(facultadId, lectivoId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comparativopre.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generatematriculados/{facultadId}/{lectivoId}")
	public ResponseEntity<Resource> generateMatriculados(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId) throws FileNotFoundException {
		String filename = service.generateMatriculados(facultadId, lectivoId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comparativopre.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generateinscriptocurso/{lectivoId}")
	public ResponseEntity<Resource> generateInscriptoCurso(@PathVariable Integer lectivoId)
			throws FileNotFoundException {
		String filename = service.generateInscriptoCurso(lectivoId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=inscriptocurso.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generateejercicioingreso/{ejercicioId}")
	public ResponseEntity<Resource> generateEjercicioIngreso(@PathVariable Integer ejercicioId)
			throws FileNotFoundException {
		String filename = service.generateEjercicioIngreso(ejercicioId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ingresos.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generateSuspendido/{facultadId}/{geograficaId}")
	public ResponseEntity<Resource> generateSuspendido(@PathVariable Integer facultadId,
			@PathVariable Integer geograficaId) throws FileNotFoundException {
		String filename = service.generateSuspendido(facultadId, geograficaId);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=suspendidos.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

	@GetMapping("/generateContratados/{anho}/{mes}")
	public ResponseEntity<Resource> generateContratados(@PathVariable Integer anho, @PathVariable Integer mes)
			throws FileNotFoundException {
		String filename = service.generateContratados(anho, mes);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contratados.xlsx");
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

}
