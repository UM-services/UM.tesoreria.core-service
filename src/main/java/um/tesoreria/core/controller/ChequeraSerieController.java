/**
 *
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

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

import um.tesoreria.core.kotlin.model.ChequeraSerie;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
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
public class ChequeraSerieController {

    private final ChequeraSerieService service;
    private final ChequeraCuotaService chequeraCuotaService;

    public ChequeraSerieController(ChequeraSerieService service, ChequeraCuotaService chequeraCuotaService) {
        this.service = service;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @GetMapping("/lectivo/{facultadId}/{lectivoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByLectivo(@PathVariable Integer facultadId, @PathVariable Integer lectivoId) {
        log.debug("Processing ChequeraSerieController.findAllByLectivo");
        return ResponseEntity.ok(service.findAllByLectivoIdAndFacultadId(lectivoId, facultadId));
    }

    @GetMapping("/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByPersona(@PathVariable BigDecimal personaId,
                                                                @PathVariable Integer documentoId) {
        return new ResponseEntity<>(service.findAllByPersona(personaId, documentoId), HttpStatus.OK);
    }

    @GetMapping("/personaextended/{personaId}/{documentoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByPersonaExtended(@PathVariable BigDecimal personaId,
                                                                        @PathVariable Integer documentoId) {
        return new ResponseEntity<>(service.findAllByPersonaExtended(personaId, documentoId, chequeraCuotaService),
                HttpStatus.OK);
    }

    @GetMapping("/facultad/{personaId}/{documentoId}/{facultadId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByFacultad(@PathVariable BigDecimal personaId,
                                                                 @PathVariable Integer documentoId, @PathVariable Integer facultadId) {
        return new ResponseEntity<>(service.findAllByFacultad(personaId, documentoId, facultadId),
                HttpStatus.OK);
    }

    @GetMapping("/facultadextended/{personaId}/{documentoId}/{facultadId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByFacultadExtended(@PathVariable BigDecimal personaId,
                                                                         @PathVariable Integer documentoId, @PathVariable Integer facultadId) {
        return new ResponseEntity<>(
                service.findAllByFacultadExtended(personaId, documentoId, facultadId, chequeraCuotaService), HttpStatus.OK);
    }

    @GetMapping("/personaLectivo/{personaId}/{documentoId}/{lectivoId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByPersonaLectivo(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
        return new ResponseEntity<>(service.findAllByPersonaLectivo(personaId, documentoId, lectivoId), HttpStatus.OK);
    }

    @GetMapping("/incompletas/{lectivoId}/{facultadId}/{geograficaId}")
    public ResponseEntity<List<ChequeraIncompleta>> findAllIncompletas(@PathVariable Integer lectivoId,
                                                                       @PathVariable Integer facultadId, @PathVariable Integer geograficaId) {
        return new ResponseEntity<>(
                service.findAllIncompletas(lectivoId, facultadId, geograficaId), HttpStatus.OK);
    }

    @GetMapping("/altas/{lectivoId}/{facultadId}/{geograficaId}")
    public ResponseEntity<List<ChequeraSerieAlta>> findAllAltas(@PathVariable Integer lectivoId,
                                                                @PathVariable Integer facultadId, @PathVariable Integer geograficaId) {
        return new ResponseEntity<>(service.findAllAltas(lectivoId, facultadId, geograficaId),
                HttpStatus.OK);
    }

    @GetMapping("/altasFull/{lectivoId}/{facultadId}/{geograficaId}/{tipoChequeraId}/{fechaDesdePrimerVencimiento}")
    public ResponseEntity<List<ChequeraSerieAltaFull>> findAllAltasFull(@PathVariable Integer lectivoId,
                                                                        @PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer tipoChequeraId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaDesdePrimerVencimiento) {
        return new ResponseEntity<>(service.findAllAltasFull(lectivoId, facultadId, geograficaId, tipoChequeraId, fechaDesdePrimerVencimiento, chequeraCuotaService),
                HttpStatus.OK);
    }

    @GetMapping("/bynumber/{facultadId}/{chequeraserieId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByNumber(@PathVariable Integer facultadId,
                                                               @PathVariable Long chequeraserieId) {
        return new ResponseEntity<>(service.findAllByNumber(facultadId, chequeraserieId),
                HttpStatus.OK);
    }

    @PostMapping("/documentos/{facultadId}/{lectivoId}/{geograficaId}")
    public ResponseEntity<List<ChequeraSerie>> findAllByDocumentos(@PathVariable Integer facultadId,
                                                                   @PathVariable Integer lectivoId, @PathVariable Integer geograficaId,
                                                                   @RequestBody List<BigDecimal> personaIds) {
        return new ResponseEntity<>(
                service.findAllByDocumentos(facultadId, lectivoId, geograficaId, personaIds), HttpStatus.OK);
    }

    @GetMapping("/cbu/{cbu}/{debitoTipoId}")
    public ResponseEntity<List<ChequeraKey>> findAllByCbu(@PathVariable String cbu,
                                                          @PathVariable Integer debitoTipoId) {
        return new ResponseEntity<>(service.findAllByCbu(cbu, debitoTipoId), HttpStatus.OK);
    }

    @GetMapping("/visa/{numeroTarjeta}/{debitoTipoId}")
    public ResponseEntity<List<ChequeraKey>> findAllByVisa(@PathVariable String numeroTarjeta,
                                                           @PathVariable Integer debitoTipoId) {
        return new ResponseEntity<>(service.findAllByVisa(numeroTarjeta, debitoTipoId), HttpStatus.OK);
    }

    @GetMapping("/{chequeraId}")
    public ResponseEntity<ChequeraSerie> findByChequeraId(@PathVariable Long chequeraId) {
        return new ResponseEntity<>(service.findByChequeraId(chequeraId), HttpStatus.OK);
    }

    @GetMapping("/extended/{chequeraId}")
    public ResponseEntity<ChequeraSerie> findByChequeraIdExtended(@PathVariable Long chequeraId) {
        return new ResponseEntity<>(service.findByChequeraIdExtended(chequeraId, chequeraCuotaService), HttpStatus.OK);
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerie> findByUnique(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId),
                HttpStatus.OK);
    }

    @GetMapping("/uniqueextended/{facultadId}/{tipochequeraId}/{chequeraserieId}")
    public ResponseEntity<ChequeraSerie> findByUniqueExtended(@PathVariable Integer facultadId,
                                                              @PathVariable Integer tipochequeraId, @PathVariable Long chequeraserieId) {
        return new ResponseEntity<>(
                service.findByUniqueExtended(facultadId, tipochequeraId, chequeraserieId, chequeraCuotaService), HttpStatus.OK);
    }

    @GetMapping("/setpaypertic/{facultadId}/{tipochequeraId}/{chequeraserieId}/{flag}")
    public ResponseEntity<ChequeraSerie> setPayPerTic(@PathVariable Integer facultadId,
                                                      @PathVariable Integer tipochequeraId, @PathVariable Long chequeraserieId, @PathVariable Byte flag) {
        return new ResponseEntity<>(
                service.setPayPerTic(facultadId, tipochequeraId, chequeraserieId, flag), HttpStatus.OK);
    }

    @PutMapping("/{chequeraId}")
    public ResponseEntity<ChequeraSerie> update(@RequestBody ChequeraSerie chequeraserie,
                                                @PathVariable Long chequeraId) {
        return new ResponseEntity<>(service.update(chequeraserie, chequeraId), HttpStatus.OK);
    }

    @PostMapping("/mark/sent/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerie> markSent(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                         @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.markSent(facultadId, tipoChequeraId, chequeraSerieId), HttpStatus.OK);
    }

}
