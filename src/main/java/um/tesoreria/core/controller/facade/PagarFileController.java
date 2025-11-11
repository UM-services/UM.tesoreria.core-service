/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.io.IOException;
import java.time.OffsetDateTime;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.facade.PagarFileService;

import static um.tesoreria.core.util.Tool.generateFile;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/pagarfile")
@RequiredArgsConstructor
public class PagarFileController {

	private final PagarFileService service;

	@GetMapping("/generate/{desde}/{hasta}")
	public ResponseEntity<Resource> generateFiles(
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta) throws IOException {
		String filename = service.generateFiles(desde, hasta);
		return generateFile(filename, filename);
	}

}
