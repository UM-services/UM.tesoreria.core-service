/**
 * 
 */
package um.tesoreria.core.controller.facade;

import java.io.IOException;
import java.time.OffsetDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.service.facade.PayPerTicFileService;
import um.tesoreria.core.util.transfer.FileInfo;

import static um.tesoreria.core.util.Tool.generateFile;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/payperticfile")
public class PayPerTicFileController {

	private final PayPerTicFileService service;

	@Autowired
	public PayPerTicFileController(PayPerTicFileService service) {
		this.service = service;
	}

	@GetMapping("/generate/{filename_return}/{desde}/{hasta}")
	public ResponseEntity<Resource> generate(@PathVariable String filename_return,
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta) throws IOException {
		return generateFile(service.generate(desde, hasta), filename_return);
	}

	@PostMapping("/upload")
	public ResponseEntity<Void> upload(@RequestBody FileInfo fileinfo) {
		service.upload(fileinfo);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

}
