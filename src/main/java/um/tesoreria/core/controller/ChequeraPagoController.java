/**
 *
 */
package um.tesoreria.core.controller;

import um.tesoreria.core.kotlin.model.ChequeraPago;
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

import um.tesoreria.core.exception.ChequeraPagoException;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraPagoService;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * @author daniel
 */
@RestController
@RequestMapping({"/chequerapago", "/api/tesoreria/core/chequeraPago"})
public class ChequeraPagoController {

    private final ChequeraPagoService service;
    private final ChequeraCuotaService chequeraCuotaService;

    @Autowired
    public ChequeraPagoController(ChequeraPagoService service, ChequeraCuotaService chequeraCuotaService) {
        this.service = service;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @GetMapping("/pendientesFactura/{fechaPago}")
    public ResponseEntity<List<ChequeraPago>> pendientesFactura(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaPago) {
        return new ResponseEntity<>(service.pendientesFactura(fechaPago, chequeraCuotaService), HttpStatus.OK);
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraPago>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, chequeraCuotaService), HttpStatus.OK);
    }

    @GetMapping("/{chequeraPagoId}")
    public ResponseEntity<ChequeraPago> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
        try {
            return new ResponseEntity<>(service.findByChequeraPagoId(chequeraPagoId, chequeraCuotaService), HttpStatus.OK);
        } catch (ChequeraPagoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraPago> add(@RequestBody ChequeraPago chequeraPago) {
        return new ResponseEntity<>(service.add(chequeraPago, chequeraCuotaService), HttpStatus.OK);
    }

}
