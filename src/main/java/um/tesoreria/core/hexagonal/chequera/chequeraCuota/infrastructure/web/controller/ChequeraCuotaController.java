package um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.controller;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaDeudaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.dto.ChequeraCuotaResponse;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.mapper.ChequeraCuotaDtoMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.mapper.ChequeraSerieMapper;
import um.tesoreria.core.kotlin.model.view.ChequeraCuotaDeuda;
import um.tesoreria.core.model.dto.DeudaChequeraDto;
import um.tesoreria.core.model.internal.CuotaPeriodoDto;

@RestController
@RequestMapping({"/chequeraCuota", "/api/tesoreria/core/chequeraCuota"})
@RequiredArgsConstructor
public class ChequeraCuotaController {

    private final ChequeraCuotaService service;
    private final ChequeraCuotaDeudaService chequeraCuotaDeudaService;
    private final ChequeraSerieService chequeraSerieService;
    private final CalculateDeudaUseCase calculateDeudaUseCase;
    private final ChequeraCuotaDtoMapper chequeraCuotaDtoMapper;

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuotaResponse>> findAllByChequera(@PathVariable Integer facultadId,
                                                                       @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                       @PathVariable Integer alternativaId) {
        List<ChequeraCuotaResponse> responses = service.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
                tipoChequeraId, chequeraSerieId, alternativaId).stream()
                .map(chequeraCuotaDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/chequera/pendientes/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuotaResponse>> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaIdPendientes(@PathVariable Integer facultadId,
                                                                                                                                       @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                                                                                       @PathVariable Integer alternativaId) {
        List<ChequeraCuotaResponse> responses = service.findAllPendientes(facultadId, tipoChequeraId, chequeraSerieId, alternativaId).stream()
                .map(chequeraCuotaDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/inconsistencias/{desde}/{hasta}")
    public ResponseEntity<List<ChequeraCuotaResponse>> findAllInconsistencias(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta) {
        List<ChequeraCuotaResponse> responses = service.findAllInconsistencias(desde, hasta, false).stream()
                .map(chequeraCuotaDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
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
    public ResponseEntity<ChequeraCuotaResponse> findByChequeraCuotaId(@PathVariable Long chequeraCuotaId) {
        try {
            var domain = service.findByChequeraCuotaId(chequeraCuotaId);
            return ResponseEntity.ok(chequeraCuotaDtoMapper.toResponse(domain));
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}")
    public ResponseEntity<ChequeraCuotaResponse> findByUnique(@PathVariable Integer facultadId,
                                                            @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId, @PathVariable Integer productoId,
                                                            @PathVariable Integer alternativaId, @PathVariable Integer cuotaId) {
        try {
            var domain = service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId,
                    productoId, alternativaId, cuotaId);
            return ResponseEntity.ok(chequeraCuotaDtoMapper.toResponse(domain));
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/deuda/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<DeudaChequeraDto> calculateDeuda(@PathVariable Integer facultadId,
                                                           @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        try {
            var chequera = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
            return ResponseEntity.ok(calculateDeudaUseCase.calculateDeudaExtended(chequera));
        } catch (ChequeraSerieException e) {
            return ResponseEntity.ok(calculateDeudaUseCase.calculateDeuda(null));
        }
    }

    @GetMapping("/updateBarras/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraCuotaResponse>> updateBarras(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                                                   @PathVariable Long chequeraSerieId) {
        List<ChequeraCuotaResponse> responses = service.updateBarras(facultadId, tipoChequeraId, chequeraSerieId).stream()
                .map(chequeraCuotaDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/periodos/lectivo/{lectivoId}")
    public ResponseEntity<List<CuotaPeriodoDto>> findAllPeriodosLectivo(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllPeriodosLectivo(lectivoId));
    }

    @GetMapping("/cuotaActual/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}")
    public ResponseEntity<ChequeraCuotaResponse> getCuotaActual(@PathVariable Integer facultadId,
                                                                 @PathVariable Integer tipoChequeraId,
                                                                 @PathVariable Long chequeraSerieId,
                                                                 @PathVariable Integer productoId,
                                                                 @PathVariable Integer alternativaId) {
        try {
            var domain = service.getCuotaActual(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, OffsetDateTime.now());
            return ResponseEntity.ok(chequeraCuotaDtoMapper.toResponse(domain));
        } catch (ChequeraCuotaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
