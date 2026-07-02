package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieRequest;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.dto.ChequeraSerieResponse;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.mapper.ChequeraSerieDtoMapper;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.model.view.ChequeraKey;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;

@RestController
@RequestMapping({"/chequeraserie", "/api/tesoreria/core/chequeraSerie"})
@Slf4j
@RequiredArgsConstructor
public class ChequeraSerieController {

    private final ChequeraSerieService service;
    private final ChequeraCuotaService chequeraCuotaService;
    private final ChequeraSerieDtoMapper chequeraSerieDtoMapper;

    @GetMapping("/lectivo/{facultadId}/{lectivoId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByLectivo(@PathVariable Integer facultadId,
                                                                       @PathVariable Integer lectivoId) {
        log.debug("Processing ChequeraSerieController.findAllByLectivo");
        List<ChequeraSerieResponse> responses = service.findAllByLectivoIdAndFacultadId(lectivoId, facultadId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/sede/facultad/{facultadId}/lectivo/{lectivoId}/geografica/{geograficaId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllBySede(@PathVariable Integer facultadId,
                                                                   @PathVariable Integer lectivoId,
                                                                   @PathVariable Integer geograficaId) {
        List<ChequeraSerieResponse> responses = service.findAllBySede(facultadId, lectivoId, geograficaId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByPersona(@PathVariable BigDecimal personaId,
                                                                       @PathVariable Integer documentoId) {
        List<ChequeraSerieResponse> responses = service.findAllByPersona(personaId, documentoId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/personaextended/{personaId}/{documentoId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByPersonaExtended(@PathVariable BigDecimal personaId,
                                                                               @PathVariable Integer documentoId) {
        List<ChequeraSerieResponse> responses = service.findAllByPersonaExtended(personaId, documentoId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/facultad/{personaId}/{documentoId}/{facultadId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByFacultad(@PathVariable BigDecimal personaId,
                                                                        @PathVariable Integer documentoId,
                                                                        @PathVariable Integer facultadId) {
        List<ChequeraSerieResponse> responses = service.findAllByFacultad(personaId, documentoId, facultadId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/facultadextended/{personaId}/{documentoId}/{facultadId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByFacultadExtended(@PathVariable BigDecimal personaId,
                                                                                @PathVariable Integer documentoId,
                                                                                @PathVariable Integer facultadId) {
        List<ChequeraSerieResponse> responses = service.findAllByFacultadExtended(personaId, documentoId, facultadId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/personaLectivo/{personaId}/{documentoId}/{lectivoId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByPersonaLectivo(@PathVariable BigDecimal personaId,
                                                                              @PathVariable Integer documentoId,
                                                                              @PathVariable Integer lectivoId) {
        List<ChequeraSerieResponse> responses = service.findAllByPersonaLectivo(personaId, documentoId, lectivoId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/incompletas/{lectivoId}/{facultadId}/{geograficaId}")
    public ResponseEntity<List<ChequeraIncompleta>> findAllIncompletas(@PathVariable Integer lectivoId,
                                                                       @PathVariable Integer facultadId,
                                                                       @PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllIncompletas(lectivoId, facultadId, geograficaId));
    }

    @GetMapping("/altas/{lectivoId}/{facultadId}/{geograficaId}")
    public ResponseEntity<List<ChequeraSerieAlta>> findAllAltas(@PathVariable Integer lectivoId,
                                                                @PathVariable Integer facultadId,
                                                                @PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllAltas(lectivoId, facultadId, geograficaId));
    }

    @GetMapping("/altasFull/{lectivoId}/{facultadId}/{geograficaId}/{tipoChequeraId}/{fechaDesdePrimerVencimiento}")
    public ResponseEntity<List<ChequeraSerieAltaFull>> findAllAltasFull(@PathVariable Integer lectivoId,
                                                                        @PathVariable Integer facultadId,
                                                                        @PathVariable Integer geograficaId,
                                                                        @PathVariable Integer tipoChequeraId,
                                                                        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaDesdePrimerVencimiento) {
        return ResponseEntity.ok(service.findAllAltasFull(lectivoId, facultadId, geograficaId, tipoChequeraId, fechaDesdePrimerVencimiento, chequeraCuotaService));
    }

    @GetMapping("/bynumber/{facultadId}/{chequeraserieId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByNumber(@PathVariable Integer facultadId,
                                                                      @PathVariable Long chequeraserieId) {
        List<ChequeraSerieResponse> responses = service.findAllByNumber(facultadId, chequeraserieId).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/documentos/{facultadId}/{lectivoId}/{geograficaId}")
    public ResponseEntity<List<ChequeraSerieResponse>> findAllByDocumentos(@PathVariable Integer facultadId,
                                                                          @PathVariable Integer lectivoId,
                                                                          @PathVariable Integer geograficaId,
                                                                          @RequestBody List<BigDecimal> personaIds) {
        List<ChequeraSerieResponse> responses = service.findAllByDocumentos(facultadId, lectivoId, geograficaId, personaIds).stream()
                .map(chequeraSerieDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/cbu/{cbu}/{debitoTipoId}")
    public ResponseEntity<List<ChequeraKey>> findAllByCbu(@PathVariable String cbu,
                                                          @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllByCbu(cbu, debitoTipoId));
    }

    @GetMapping("/visa/{numeroTarjeta}/{debitoTipoId}")
    public ResponseEntity<List<ChequeraKey>> findAllByVisa(@PathVariable String numeroTarjeta,
                                                           @PathVariable Integer debitoTipoId) {
        return ResponseEntity.ok(service.findAllByVisa(numeroTarjeta, debitoTipoId));
    }

    @GetMapping("/{chequeraId}")
    public ResponseEntity<ChequeraSerieResponse> findByChequeraId(@PathVariable Long chequeraId) {
        return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(service.findByChequeraId(chequeraId)));
    }

    @GetMapping("/extended/{chequeraId}")
    public ResponseEntity<ChequeraSerieResponse> findByChequeraIdExtended(@PathVariable Long chequeraId) {
        return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(service.findByChequeraIdExtended(chequeraId)));
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerieResponse> findByUnique(@PathVariable Integer facultadId,
                                                             @PathVariable Integer tipoChequeraId,
                                                             @PathVariable Long chequeraSerieId) {
        try {
            return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId)));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/uniqueextended/{facultadId}/{tipochequeraId}/{chequeraserieId}")
    public ResponseEntity<ChequeraSerieResponse> findByUniqueExtended(@PathVariable Integer facultadId,
                                                                     @PathVariable Integer tipochequeraId,
                                                                     @PathVariable Long chequeraserieId) {
        try {
            return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(service.findByUniqueExtended(facultadId, tipochequeraId, chequeraserieId)));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/setpaypertic/{facultadId}/{tipochequeraId}/{chequeraserieId}/{flag}")
    public ResponseEntity<ChequeraSerieResponse> setPayPerTic(@PathVariable Integer facultadId,
                                                             @PathVariable Integer tipochequeraId,
                                                             @PathVariable Long chequeraserieId,
                                                             @PathVariable Byte flag) {
        return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(service.setPayPerTic(facultadId, tipochequeraId, chequeraserieId, flag)));
    }

    @PutMapping("/{chequeraId}")
    public ResponseEntity<ChequeraSerieResponse> update(@RequestBody ChequeraSerieRequest chequeraserieRequest,
                                                      @PathVariable Long chequeraId) {
        ChequeraSerie domain = chequeraSerieDtoMapper.toDomain(chequeraserieRequest);
        ChequeraSerie updatedDomain = service.update(domain, chequeraId);
        return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(updatedDomain));
    }

    @PostMapping("/mark/sent/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerieResponse> markSent(@PathVariable Integer facultadId,
                                                         @PathVariable Integer tipoChequeraId,
                                                         @PathVariable Long chequeraSerieId) {
        return ResponseEntity.ok(chequeraSerieDtoMapper.toResponse(service.markSent(facultadId, tipoChequeraId, chequeraSerieId)));
    }

    @GetMapping("/resumen/lectivo/{lectivoId}")
    public ResponseEntity<List<FacultadSedeChequeraDto>> resumenLectivo(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.resumenLectivo(lectivoId));
    }

}
