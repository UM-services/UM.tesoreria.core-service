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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ar.edu.um.tesoreria.rest.model.LectivoTotalImputacion;
import ar.edu.um.tesoreria.rest.service.LectivoTotalImputacionService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/lectivototalimputacion")
public class LectivoTotalImputacionController {

	@Autowired
	private LectivoTotalImputacionService service;

	@GetMapping("/tipo/{facultadId}/{lectivoId}/{tipochequeraId}")
	public ResponseEntity<List<LectivoTotalImputacion>> findAllByTipo(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipochequeraId) {
		return new ResponseEntity<List<LectivoTotalImputacion>>(
				service.findAllByTipo(facultadId, lectivoId, tipochequeraId), HttpStatus.OK);
	}

	@GetMapping("/producto/{facultadId}/{lectivoId}/{tipochequeraId}/{productoId}")
	public ResponseEntity<LectivoTotalImputacion> findByProducto(@PathVariable Integer facultadId,
			@PathVariable Integer lectivoId, @PathVariable Integer tipochequeraId, @PathVariable Integer productoId) {
		return new ResponseEntity<LectivoTotalImputacion>(
				service.findByProducto(facultadId, lectivoId, tipochequeraId, productoId), HttpStatus.OK);
	}

	@PostMapping("/")
	public ResponseEntity<LectivoTotalImputacion> add(@RequestBody LectivoTotalImputacion lectivoTotalImputacion) {
		return new ResponseEntity<LectivoTotalImputacion>(service.add(lectivoTotalImputacion), HttpStatus.OK);
	}

	@PutMapping("/{lectivoTotalImputacionId}")
	public ResponseEntity<LectivoTotalImputacion> update(@RequestBody LectivoTotalImputacion lectivoTotalImputacion,
			@PathVariable Long lectivoTotalImputacionId) {
		return new ResponseEntity<LectivoTotalImputacion>(
				service.update(lectivoTotalImputacion, lectivoTotalImputacionId), HttpStatus.OK);
	}

}
