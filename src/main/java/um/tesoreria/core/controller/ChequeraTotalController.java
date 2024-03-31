/**
 * 
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import um.tesoreria.core.model.ChequeraTotal;
import um.tesoreria.core.service.ChequeraTotalService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequeratotal")
public class ChequeraTotalController {
	
	@Autowired
	private ChequeraTotalService service;
	
	@GetMapping("/chequera/{facultadId}/{tipochequeraId}/{chequeraserieId}")
	public ResponseEntity<List<ChequeraTotal>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipochequeraId, @PathVariable Long chequeraserieId) {
		return new ResponseEntity<List<ChequeraTotal>>(service.findAllByChequera(facultadId, tipochequeraId, chequeraserieId), HttpStatus.OK);
	}
}
