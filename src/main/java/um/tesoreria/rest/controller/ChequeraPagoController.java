/**
 *
 */
package um.tesoreria.rest.controller;

import um.tesoreria.rest.kotlin.model.ChequeraPago;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.rest.exception.ChequeraPagoException;
import um.tesoreria.rest.service.ChequeraPagoService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequerapago")
public class ChequeraPagoController {

    @Autowired
    private ChequeraPagoService service;

    @GetMapping("/pendientesFactura/{fechaPago}")
    public ResponseEntity<List<ChequeraPago>> pendientesFactura(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaPago) {
        return new ResponseEntity<List<ChequeraPago>>(service.pendientesFactura(fechaPago), HttpStatus.OK);
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraPago>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<List<ChequeraPago>>(service.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId), HttpStatus.OK);
    }

    @GetMapping("/{chequeraPagoId}")
    public ResponseEntity<ChequeraPago> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
        try {
            return new ResponseEntity<ChequeraPago>(service.findByChequeraPagoId(chequeraPagoId), HttpStatus.OK);
        } catch (ChequeraPagoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraPago> add(@RequestBody ChequeraPago chequeraPago) {
        return new ResponseEntity<ChequeraPago>(service.add(chequeraPago), HttpStatus.OK);
    }

}
