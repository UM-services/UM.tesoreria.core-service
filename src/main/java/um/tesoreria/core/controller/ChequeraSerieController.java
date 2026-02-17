/**
 *
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;
import um.tesoreria.core.model.view.ChequeraIncompleta;
import um.tesoreria.core.model.view.ChequeraKey;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraSerieService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/chequeraserie", "/api/tesoreria/core/chequeraSerie"})
@Slf4j
@RequiredArgsConstructor
public class ChequeraSerieController {

    private final ChequeraSerieService service;
    private final ChequeraCuotaService chequeraCuotaService;

    @GetMapping("/lectivo/{facultadId}/{lectivoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByLectivo(@PathVariable Integer facultadId,
                                                                @PathVariable Integer lectivoId) {
        log.debug("Processing ChequeraSerieController.findAllByLectivo");
        return ResponseEntity.ok(service.findAllByLectivoIdAndFacultadId(lectivoId, facultadId));
    }

    @GetMapping("/sede/facultad/{facultadId}/lectivo/{lectivoId}/geografica/{geograficaId}")
    public ResponseEntity<List<ChequeraSerie>> findAllBySede(@PathVariable Integer facultadId,
                                                             @PathVariable Integer lectivoId,
                                                             @PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllBySede(facultadId, lectivoId, geograficaId));
    }

    @GetMapping("/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByPersona(@PathVariable BigDecimal personaId,
                                                                @PathVariable Integer documentoId) {
        return ResponseEntity.ok(service.findAllByPersona(personaId, documentoId));
    }

    @GetMapping("/personaextended/{personaId}/{documentoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByPersonaExtended(@PathVariable BigDecimal personaId,
                                                                        @PathVariable Integer documentoId) {
        return new ResponseEntity<>(service.findAllByPersonaExtended(personaId, documentoId),
                HttpStatus.OK);
    }

    @GetMapping("/facultad/{personaId}/{documentoId}/{facultadId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByFacultad(@PathVariable BigDecimal personaId,
                                                                 @PathVariable Integer documentoId,
                                                                 @PathVariable Integer facultadId) {
        return ResponseEntity.ok(service.findAllByFacultad(personaId, documentoId, facultadId));
    }

    @GetMapping("/facultadextended/{personaId}/{documentoId}/{facultadId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByFacultadExtended(@PathVariable BigDecimal personaId,
                                                                         @PathVariable Integer documentoId,
                                                                         @PathVariable Integer facultadId) {
        return ResponseEntity.ok(service.findAllByFacultadExtended(personaId, documentoId, facultadId));
    }

    @GetMapping("/personaLectivo/{personaId}/{documentoId}/{lectivoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByPersonaLectivo(@PathVariable BigDecimal personaId,
                                                                       @PathVariable Integer documentoId,
                                                                       @PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllByPersonaLectivo(personaId, documentoId, lectivoId));
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
    public ResponseEntity<List<ChequeraSerie>> findAllByNumber(@PathVariable Integer facultadId,
                                                               @PathVariable Long chequeraserieId) {
        return ResponseEntity.ok(service.findAllByNumber(facultadId, chequeraserieId));
    }

    @PostMapping("/documentos/{facultadId}/{lectivoId}/{geograficaId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByDocumentos(@PathVariable Integer facultadId,
                                                                   @PathVariable Integer lectivoId,
                                                                   @PathVariable Integer geograficaId,
                                                                   @RequestBody List<BigDecimal> personaIds) {
        return ResponseEntity.ok(service.findAllByDocumentos(facultadId, lectivoId, geograficaId, personaIds));
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
    public ResponseEntity<ChequeraSerie> findByChequeraId(@PathVariable Long chequeraId) {
        return ResponseEntity.ok(service.findByChequeraId(chequeraId));
    }

    @GetMapping("/extended/{chequeraId}")
    public ResponseEntity<ChequeraSerie> findByChequeraIdExtended(@PathVariable Long chequeraId) {
        return ResponseEntity.ok(service.findByChequeraIdExtended(chequeraId));
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerie> findByUnique(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipoChequeraId,
                                                      @PathVariable Long chequeraSerieId) {
        try {
            return ResponseEntity.ok(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/uniqueextended/{facultadId}/{tipochequeraId}/{chequeraserieId}")
    public ResponseEntity<ChequeraSerie> findByUniqueExtended(@PathVariable Integer facultadId,
                                                              @PathVariable Integer tipochequeraId,
                                                              @PathVariable Long chequeraserieId) {
        try {
            return ResponseEntity.ok(service.findByUniqueExtended(facultadId, tipochequeraId, chequeraserieId));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/setpaypertic/{facultadId}/{tipochequeraId}/{chequeraserieId}/{flag}")
    public ResponseEntity<ChequeraSerie> setPayPerTic(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipochequeraId,
                                                      @PathVariable Long chequeraserieId,
                                                      @PathVariable Byte flag) {
        return ResponseEntity.ok(service.setPayPerTic(facultadId, tipochequeraId, chequeraserieId, flag));
    }

    @PutMapping("/{chequeraId}")
    public ResponseEntity<ChequeraSerie> update(@RequestBody ChequeraSerie chequeraserie,
                                                @PathVariable Long chequeraId) {
        return ResponseEntity.ok(service.update(chequeraserie, chequeraId));
    }

    @PostMapping("/mark/sent/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerie> markSent(@PathVariable Integer facultadId,
                                                  @PathVariable Integer tipoChequeraId,
                                                  @PathVariable Long chequeraSerieId) {
        return ResponseEntity.ok(service.markSent(facultadId, tipoChequeraId, chequeraSerieId));
    }

    @GetMapping("/resumen/lectivo/{lectivoId}")
    public ResponseEntity<List<FacultadSedeChequeraDto>> resumenLectivo(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.resumenLectivo(lectivoId));
    }

}
