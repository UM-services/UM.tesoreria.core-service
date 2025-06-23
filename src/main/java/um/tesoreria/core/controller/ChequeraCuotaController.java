/**
 *
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;
import um.tesoreria.core.service.view.ChequeraCuotaDeudaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.ChequeraCuotaException;
import um.tesoreria.core.model.dto.DeudaChequeraDto;
import um.tesoreria.core.service.ChequeraCuotaService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/chequeraCuota", "/api/tesoreria/core/chequeraCuota"})
public class ChequeraCuotaController {

    public final ChequeraCuotaService service;
    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;

    public ChequeraCuotaController(ChequeraCuotaService service, ChequeraCuotaDeudaService chequeraCuotaDeudaService) {
        this.service = service;
        this.chequeraCuotaDeudaService = chequeraCuotaDeudaService;
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuota>> findAllByChequera(@PathVariable Integer facultadId,
                                                                 @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                 @PathVariable Integer alternativaId) {
        return new ResponseEntity<>(
                service.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
                        tipoChequeraId, chequeraSerieId, alternativaId),
                HttpStatus.OK);
    }

    @GetMapping("/chequera/pendientes/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuota>> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdPendentes(@PathVariable Integer facultadId,
                                                                                                                                @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                                                                                @PathVariable Integer alternativaId) {
        return new ResponseEntity<>(
                service.findAllPendientes(facultadId, tipoChequeraId, chequeraSerieId, alternativaId),
                HttpStatus.OK);
    }

    @GetMapping("/inconsistencias/{desde}/{hasta}")
        public ResponseEntity<List<ChequeraCuota>> findAllInconsistencias(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return new ResponseEntity<>(service.findAllInconsistencias(desde, hasta, false), HttpStatus.OK);
    }

    @GetMapping("/deudaRango/{desde}/{hasta}/{reduced}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta, @PathVariable Boolean reduced) {
        return new ResponseEntity<>(chequeraCuotaDeudaService.findAllByRango(desde, hasta, reduced, null, service), HttpStatus.OK);
//        return new ResponseEntity<>(chequeraCuotaDeudaService.findQuinterosByRango(desde, hasta, reduced, null, service), HttpStatus.OK);
    }

    @GetMapping("/deudaRango/grupo/{grupo}/{desde}/{hasta}/{reduced}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllGrupoDeudaRango(@PathVariable Integer grupo, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta, @PathVariable Boolean reduced) {
        return new ResponseEntity<>(chequeraCuotaDeudaService.findAllByGrupoRango(grupo, desde, hasta, reduced, null, service), HttpStatus.OK);
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
    public ResponseEntity<DeudaChequeraDto> calculateDeuda(@PathVariable Integer facultadId,
                                                           @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.calculateDeuda(facultadId, tipoChequeraId, chequeraSerieId),
                HttpStatus.OK);
    }

    @GetMapping("/updateBarras/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraCuota>> updateBarras(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                             @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.updateBarras(facultadId, tipoChequeraId, chequeraSerieId),
                HttpStatus.OK);
    }

    @GetMapping("/periodos/lectivo/{lectivoId}")
    public ResponseEntity<List<CuotaPeriodoDto>> findAllPeriodosLectivo(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllPeriodosLectivo(lectivoId));
    }

}
