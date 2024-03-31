/**
 * 
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.PayPerTic;
import um.tesoreria.core.service.PayPerTicService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/paypertic")
public class PayPerTicController {
	@Autowired
	private PayPerTicService service;

	@GetMapping("/periodo/{desde}/{hasta}")
	public ResponseEntity<List<PayPerTic>> findAllByPeriodo(
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime desde,
			@PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime hasta) {
		return new ResponseEntity<List<PayPerTic>>(service.findAllByPeriodo(desde, hasta), HttpStatus.OK);
	}
	
	@PutMapping("/{payperticId}")
	public ResponseEntity<PayPerTic> update(@RequestBody PayPerTic paypertic, @PathVariable String payperticId) {
		return new ResponseEntity<PayPerTic>(service.update(paypertic, payperticId), HttpStatus.OK);
	}
}
