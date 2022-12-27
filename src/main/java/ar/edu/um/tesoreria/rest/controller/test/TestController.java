/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller.test;

import java.math.BigDecimal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.util.Tool;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/test")
public class TestController {

	@GetMapping("/")
	public ResponseEntity<String> test() {
		String value = Tool.number2Text(new BigDecimal(371261234.50));
		return new ResponseEntity<String>(value, HttpStatus.OK);
	}
}
