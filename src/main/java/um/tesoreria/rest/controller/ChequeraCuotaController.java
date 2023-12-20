/**
 *
 */
package um.tesoreria.rest.controller;

import java.time.OffsetDateTime;
import java.util.List;

import um.tesoreria.rest.kotlin.model.ChequeraCuota;
import um.tesoreria.rest.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.rest.service.view.ChequeraCuotaDeudaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.rest.exception.ChequeraCuotaException;
import um.tesoreria.rest.model.dto.DeudaChequera;
import um.tesoreria.rest.service.ChequeraCuotaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/chequeraCuota")
public class ChequeraCuotaController {

    public final ChequeraCuotaService service;

    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    @Autowired
    public ChequeraCuotaController(ChequeraCuotaService service, ChequeraCuotaDeudaService chequeraCuotaDeudaService) {
        this.service = service;
        this.chequeraCuotaDeudaService = chequeraCuotaDeudaService;
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuota>> findAllByChequera(@PathVariable Integer facultadId,
                                                                 @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                 @PathVariable Integer alternativaId) {
        return new ResponseEntity<>(
                service.findAllByFacultadIdAndTipochequeraIdAndChequeraserieIdAndAlternativaId(facultadId,
                        tipoChequeraId, chequeraSerieId, alternativaId),
                HttpStatus.OK);
    }

    @GetMapping("/inconsistencias/{desde}/{hasta}")
        public ResponseEntity<List<ChequeraCuota>> findAllInconsistencias(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return new ResponseEntity<>(service.findAllInconsistencias(desde, hasta, false), HttpStatus.OK);
    }

    @GetMapping("/deudaRango/{desde}/{hasta}/{reduced}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta, @PathVariable Boolean reduced) {
        return new ResponseEntity<>(chequeraCuotaDeudaService.findAllByRango(desde, hasta, reduced, null, service), HttpStatus.OK);
    }

    @GetMapping("/deudaPosgradoRango/{desde}/{hasta}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaPosgradoRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return new ResponseEntity<>(chequeraCuotaDeudaService.findAllPosgradoByRango(desde, hasta, service), HttpStatus.OK);
    }

    @GetMapping("/deudaMacroRango/{desde}/{hasta}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaMacroRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return new ResponseEntity<>(chequeraCuotaDeudaService.findAllMacroByRango(desde, hasta, service), HttpStatus.OK);
    }

    @GetMapping("/{chequeraCuotaId}")
    public ResponseEntity<ChequeraCuota> findByChequeraCuotaId(@PathVariable Long chequeraCuotaId) {
        try {
            return new ResponseEntity<>(service.findByChequeraCuotaId(chequeraCuotaId), HttpStatus.OK);
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}")
    public ResponseEntity<ChequeraCuota> findByUnique(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId, @PathVariable Integer productoId,
                                                      @PathVariable Integer alternativaId, @PathVariable Integer cuotaId) {
        try {
            return new ResponseEntity<>(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId,
                    productoId, alternativaId, cuotaId), HttpStatus.OK);
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/deuda/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<DeudaChequera> calculateDeuda(@PathVariable Integer facultadId,
                                                        @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.calculateDeuda(facultadId, tipoChequeraId, chequeraSerieId),
                HttpStatus.OK);
    }

}
