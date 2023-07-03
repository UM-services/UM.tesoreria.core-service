/**
 * 
 */
package um.tesoreria.rest.controller.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.rest.service.facade.PagarFileService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/pagarfile")
public class PagarFileController {

	@Autowired
	private PagarFileService service;

	@GetMapping("/generate/{desde}/{hasta}")
	public ResponseEntity<Resource> generateFiles(
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta, Boolean reduced) throws IOException {
		String filename = service.generateFiles(desde, hasta);
		File file = new File(filename);
		InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
		HttpHeaders headers = new HttpHeaders();
		headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
		headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
		headers.add("Pragma", "no-cache");
		headers.add("Expires", "0");
		return ResponseEntity.ok().headers(headers).contentLength(file.length())
				.contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
	}

}
