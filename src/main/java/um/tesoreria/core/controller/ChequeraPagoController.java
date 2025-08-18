/**
 *
 */
package um.tesoreria.core.controller;

import um.tesoreria.core.kotlin.model.ChequeraPago;
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
@RequestMapping({"/chequeraPago", "/api/tesoreria/core/chequeraPago"})
public class ChequeraPagoController {

    private final ChequeraPagoService service;
    private final ChequeraCuotaService chequeraCuotaService;

    public ChequeraPagoController(ChequeraPagoService service, ChequeraCuotaService chequeraCuotaService) {
        this.service = service;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @GetMapping("/pendientesFactura/{fechaPago}")
    public ResponseEntity<List<ChequeraPago>> pendientesFactura(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaPago) {
        return ResponseEntity.ok(service.pendientesFactura(fechaPago, chequeraCuotaService));
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraPago>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return ResponseEntity.ok(service.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, chequeraCuotaService));
    }

    @GetMapping("/fecha/acreditacion/{tipoPagoId}/{fechaAcreditacion}")
    public ResponseEntity<List<ChequeraPago>> findAllByTipoPagoIdAndFechaAcreditacion(@PathVariable Integer tipoPagoId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaAcreditacion) {
        return ResponseEntity.ok(service.findAllByTipoPagoIdAndFechaAcreditacion(tipoPagoId, fechaAcreditacion));
    }

    @GetMapping("/fecha/pago/{tipoPagoId}/{fechaPago}")
    public ResponseEntity<List<ChequeraPago>> findAllByTipoPagoIdAndFechaPago(@PathVariable Integer tipoPagoId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaPago) {
        return ResponseEntity.ok(service.findAllByTipoPagoIdAndFechaPago(tipoPagoId, fechaPago));
    }

    @GetMapping("/pagos/facultad/{facultadId}/tipoChequera/{tipoChequeraId}/lectivo/{lectivoId}")
    public ResponseEntity<List<ChequeraPago>> findAllByFacultadIdAndTipoChequeraIdAndLectivoId(@PathVariable Integer facultadId,
                                                                                               @PathVariable Integer tipoChequeraId,
                                                                                               @PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllByFacultadIdAndTipoChequeraIdAndLectivoId(facultadId, tipoChequeraId, lectivoId));
    }

    @GetMapping("/{chequeraPagoId}")
    public ResponseEntity<ChequeraPago> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
        try {
            return ResponseEntity.ok(service.findByChequeraPagoId(chequeraPagoId, chequeraCuotaService));
        } catch (ChequeraPagoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraPago> add(@RequestBody ChequeraPago chequeraPago) {
        return ResponseEntity.ok(service.add(chequeraPago, chequeraCuotaService));
    }

    @GetMapping("/search/id/mercado/pago/{idMercadoPago}")
    public ResponseEntity<ChequeraPago> findByIdMercadoPago(@PathVariable String idMercadoPago) {
        try {
            return ResponseEntity.ok(service.findByIdMercadoPago(idMercadoPago));
        } catch (ChequeraPagoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
