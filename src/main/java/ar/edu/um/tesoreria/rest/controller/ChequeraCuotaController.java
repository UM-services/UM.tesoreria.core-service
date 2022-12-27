/**
 * 
 */
package ar.edu.um.tesoreria.rest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import ar.edu.um.tesoreria.rest.exception.ChequeraCuotaNotFoundException;
import ar.edu.um.tesoreria.rest.model.ChequeraCuota;
import ar.edu.um.tesoreria.rest.model.dto.DeudaChequera;
import ar.edu.um.tesoreria.rest.service.ChequeraCuotaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequeracuota")
public class ChequeraCuotaController {

	@Autowired
	public ChequeraCuotaService service;

	@GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
	public ResponseEntity<List<ChequeraCuota>> findAllByChequera(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
			@PathVariable Integer alternativaId) {
		return new ResponseEntity<List<ChequeraCuota>>(
				service.findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(facultadId,
						tipoChequeraId, chequeraSerieId, alternativaId),
				HttpStatus.OK);
	}

	@GetMapping("/{chequeraCuotaId}")
	public ResponseEntity<ChequeraCuota> findByChequeraCuotaId(@PathVariable Long chequeraCuotaId) {
		try {
			return new ResponseEntity<ChequeraCuota>(service.findByChequeraCuotaId(chequeraCuotaId), HttpStatus.OK);
		} catch (ChequeraCuotaNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}")
	public ResponseEntity<ChequeraCuota> findByUnique(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId, @PathVariable Integer productoId,
			@PathVariable Integer alternativaId, @PathVariable Integer cuotaId) {
		try {
			return new ResponseEntity<ChequeraCuota>(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId,
					productoId, alternativaId, cuotaId), HttpStatus.OK);
		} catch (ChequeraCuotaNotFoundException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	@GetMapping("/deuda/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
	public ResponseEntity<DeudaChequera> calculateDeuda(@PathVariable Integer facultadId,
			@PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
		return new ResponseEntity<DeudaChequera>(service.calculateDeuda(facultadId, tipoChequeraId, chequeraSerieId),
				HttpStatus.OK);
	}

}
