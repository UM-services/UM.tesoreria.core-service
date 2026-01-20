/**
 *
 */
package um.tesoreria.core.controller.facade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.List;

import jakarta.mail.MessagingException;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ChequeraSerieException;
import um.tesoreria.core.exception.SpoterDataException;
import um.tesoreria.core.kotlin.model.dto.PreuniversitarioData;
import um.tesoreria.core.kotlin.model.dto.SpoterDataResponse;
import um.tesoreria.core.model.dto.ChequeraCuotaPagosDto;
import um.tesoreria.core.model.dto.ChequeraDetailDto;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.SpoterDataService;
import um.tesoreria.core.service.facade.ChequeraService;
import um.tesoreria.core.service.facade.FormulariosToPdfService;
import um.tesoreria.core.service.facade.MailChequeraService;
import um.tesoreria.core.kotlin.model.SpoterData;

/**
 * @author daniel
 */
@RestController
@RequestMapping({"/chequera", "/api/tesoreria/core/chequera"})
@RequiredArgsConstructor
public class ChequeraController {

    private final ChequeraService service;
    private final MailChequeraService mailChequeraService;
    private final FormulariosToPdfService formularioToPdfService;
    private final SpoterDataService spoterDataService;
    private final ChequeraCuotaService chequeraCuotaService;

    @DeleteMapping("/delete/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{usuarioId}")
    public ResponseEntity<Void> deleteByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                                 @PathVariable Long chequeraSerieId, @PathVariable String usuarioId) {
        service.deleteByChequera(facultadId, tipoChequeraId, chequeraSerieId, usuarioId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/sendChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{copiaInformes}")
    public ResponseEntity<String> sendChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                               @PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId,
                                               @PathVariable Boolean copiaInformes) {
        chequeraCuotaService.updateBarras(facultadId, tipoChequeraId, chequeraSerieId);
        return ResponseEntity.ok(mailChequeraService.sendChequera(facultadId, tipoChequeraId, chequeraSerieId,
                alternativaId, copiaInformes, false, false));
    }

    @GetMapping("/sendCuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{productoId}/{cuotaId}/{copiaInformes}")
    public ResponseEntity<String> sendCuota(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                            @PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId,
                                            @PathVariable Integer productoId, @PathVariable Integer cuotaId,
                                            @PathVariable Boolean copiaInformes) {
        chequeraCuotaService.updateBarras(facultadId, tipoChequeraId, chequeraSerieId);
        return ResponseEntity.ok(mailChequeraService.sendCuota(facultadId, tipoChequeraId, chequeraSerieId,
                alternativaId, productoId, cuotaId, copiaInformes, true));
    }

    @GetMapping("/notificaDeudor/{personaId}/{documentoId}")
    public ResponseEntity<String> notificaDeudor(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId)
            throws MessagingException {
        return ResponseEntity.ok(mailChequeraService.notificaDeudor(personaId, documentoId));
    }

    @GetMapping("/generatePdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<Resource> generatePdf(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                                @PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId) throws FileNotFoundException {
        String filename = formularioToPdfService.generateChequeraPdf(facultadId, tipoChequeraId, chequeraSerieId,
                alternativaId);
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=chequera.pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @GetMapping("/generateCuotaPdf/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}/{productoId}/{cuotaId}")
    public ResponseEntity<Resource> generateCuotaPdf(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                                     @PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId,
                                                     @PathVariable Integer productoId, @PathVariable Integer cuotaId) throws FileNotFoundException {
        String filename = formularioToPdfService.generateCuotaPdf(facultadId, tipoChequeraId, chequeraSerieId,
                alternativaId, productoId, cuotaId);
        File file = new File(filename);
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=cuota.pdf");
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        return ResponseEntity.ok().headers(headers).contentLength(file.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
    }

    @GetMapping("/extendDebito/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<Void> extendDebito(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                             @PathVariable Long chequeraSerieId) {
        service.extendDebito(facultadId, tipoChequeraId, chequeraSerieId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/track/{chequeraId}")
    public ResponseEntity<Void> track(@PathVariable Long chequeraId) {
        service.track(chequeraId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/spoter/{updateMailPersonal}/{responseSinEnvio}")
    public ResponseEntity<SpoterDataResponse> sendChequeraPreSpoter(@RequestBody SpoterData spoterData, @PathVariable Boolean updateMailPersonal, @PathVariable Boolean responseSinEnvio) {
        try {
            return ResponseEntity.ok(mailChequeraService.sendChequeraPreSpoter(spoterData, updateMailPersonal, responseSinEnvio));
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new SpoterDataResponse(false, e.getMessage(), null, null, null, null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/spoter/{personaId}/{documentoId}/{facultadId}/{geograficaId}/{lectivoId}")
    public ResponseEntity<SpoterData> findOne(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable Integer facultadId, @PathVariable Integer geograficaId, @PathVariable Integer lectivoId) {
        try {
            return ResponseEntity.ok(spoterDataService.findOne(personaId, documentoId, facultadId, geograficaId, lectivoId));
        } catch (SpoterDataException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/lastPre/{facultadId}/{personaId}/{documentoId}")
    public ResponseEntity<PreuniversitarioData> lastPreData(@PathVariable Integer facultadId,
                                                            @PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
        try {
            return ResponseEntity.ok(service.findLastPreData(facultadId, personaId, documentoId));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/cuotas/pagos/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuotaPagosDto>> findAllCuotaPagosByChequera(@PathVariable Integer facultadId,
                                                                                   @PathVariable Integer tipoChequeraId,
                                                                                   @PathVariable Long chequeraSerieId,
                                                                                   @PathVariable Integer alternativaId) {
        return ResponseEntity.ok(service.findAllCuotaPagosByChequera(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));
    }

    @GetMapping("/status/{personaId}/facultad/{facultadId}")
    public ResponseEntity<ChequeraDetailDto> constructStatusFromChequera(@PathVariable BigDecimal personaId, @PathVariable Integer facultadId) {
        return ResponseEntity.ok(service.constructStatusFromChequera(personaId, facultadId));
    }

}
