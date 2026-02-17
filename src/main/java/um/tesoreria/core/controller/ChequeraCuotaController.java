/**
 *
 */
package um.tesoreria.core.controller;

import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
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

import um.tesoreria.core.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.util.ChequeraSerieMapper;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/chequeraCuota", "/api/tesoreria/core/chequeraCuota"})
@RequiredArgsConstructor
public class ChequeraCuotaController {

    public final ChequeraCuotaService service;
    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;
    private final ChequeraSerieService chequeraSerieService;
    private final CalculateDeudaUseCase calculateDeudaUseCase;

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuota>> findAllByChequera(@PathVariable Integer facultadId,
                                                                 @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                 @PathVariable Integer alternativaId) {
        return ResponseEntity.ok(service.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
                tipoChequeraId, chequeraSerieId, alternativaId));
    }

    @GetMapping("/chequera/pendientes/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuota>> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdPendientes(@PathVariable Integer facultadId,
                                                                                                                                @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                                                                                @PathVariable Integer alternativaId) {
        return ResponseEntity.ok(service.findAllPendientes(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));
    }

    @GetMapping("/inconsistencias/{desde}/{hasta}")
        public ResponseEntity<List<ChequeraCuota>> findAllInconsistencias(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return ResponseEntity.ok(service.findAllInconsistencias(desde, hasta, false));
    }

    @GetMapping("/deudaRango/{desde}/{hasta}/{reduced}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta, @PathVariable Boolean reduced) {
        return ResponseEntity.ok(chequeraCuotaDeudaService.findAllByRango(desde, hasta, reduced, null, service));
    }

    @GetMapping("/deudaRango/grupo/{grupo}/{desde}/{hasta}/{reduced}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllGrupoDeudaRango(@PathVariable Integer grupo, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta, @PathVariable Boolean reduced) {
        return ResponseEntity.ok(chequeraCuotaDeudaService.findAllByGrupoRango(grupo, desde, hasta, reduced, null, service));
    }

    @GetMapping("/deudaPosgradoRango/{desde}/{hasta}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaPosgradoRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return ResponseEntity.ok(chequeraCuotaDeudaService.findAllPosgradoByRango(desde, hasta, service));
    }

    @GetMapping("/deudaMacroRango/{desde}/{hasta}")
    public ResponseEntity<List<ChequeraCuotaDeuda>> findAllDeudaMacroRango(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        return ResponseEntity.ok(chequeraCuotaDeudaService.findAllMacroByRango(desde, hasta, service));
    }

    @GetMapping("/{chequeraCuotaId}")
    public ResponseEntity<ChequeraCuota> findByChequeraCuotaId(@PathVariable Long chequeraCuotaId) {
        try {
            return ResponseEntity.ok(service.findByChequeraCuotaId(chequeraCuotaId));
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}")
    public ResponseEntity<ChequeraCuota> findByUnique(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId, @PathVariable Integer productoId,
                                                      @PathVariable Integer alternativaId, @PathVariable Integer cuotaId) {
        try {
            return ResponseEntity.ok(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId,
                    productoId, alternativaId, cuotaId));
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/deuda/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<DeudaChequeraDto> calculateDeuda(@PathVariable Integer facultadId,
                                                           @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        try {
            var chequera = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
            return ResponseEntity.ok(calculateDeudaUseCase.calculateDeudaExtended(ChequeraSerieMapper.toHexagonal(chequera)));
        } catch (um.tesoreria.core.exception.ChequeraSerieException e) {
            return ResponseEntity.ok(calculateDeudaUseCase.calculateDeuda(null));
        }
    }

    @GetMapping("/updateBarras/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraCuota>> updateBarras(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                             @PathVariable Long chequeraSerieId) {
        return ResponseEntity.ok(service.updateBarras(facultadId, tipoChequeraId, chequeraSerieId));
    }

    @GetMapping("/periodos/lectivo/{lectivoId}")
    public ResponseEntity<List<CuotaPeriodoDto>> findAllPeriodosLectivo(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllPeriodosLectivo(lectivoId));
    }

}
