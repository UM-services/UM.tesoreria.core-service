package um.tesoreria.core.controller;

import um.tesoreria.core.exception.ChequeraFacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.ChequeraFacturacionElectronica;
import um.tesoreria.core.service.ChequeraFacturacionElectronicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping({"/chequeraFacturacionElectronica", "/api/tesoreria/core/chequeraFacturacionElectronica"})
public class ChequeraFacturacionElectronicaController {

    private final ChequeraFacturacionElectronicaService service;

    public ChequeraFacturacionElectronicaController(ChequeraFacturacionElectronicaService service) {
        this.service = service;
    }

    @GetMapping("/chequera/{chequeraId}")
    public ResponseEntity<ChequeraFacturacionElectronica> findByChequeraId(@PathVariable Long chequeraId) {
        try {
            return ResponseEntity.ok(service.findByChequeraId(chequeraId));
        } catch (ChequeraFacturacionElectronicaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraFacturacionElectronica> add(@RequestBody ChequeraFacturacionElectronica chequeraFacturacionElectronica) {
        return ResponseEntity.ok(service.add(chequeraFacturacionElectronica));
    }

    @PutMapping("/{chequeraFacturacionElectronicaId}")
    public ResponseEntity<ChequeraFacturacionElectronica> update(@RequestBody ChequeraFacturacionElectronica chequeraFacturacionElectronica, @PathVariable Long chequeraFacturacionElectronicaId) {
        return ResponseEntity.ok(service.update(chequeraFacturacionElectronica, chequeraFacturacionElectronicaId));
    }

    @DeleteMapping("/{chequeraFacturacionElectronicaId}")
    public ResponseEntity<Void> deleteByChequeraFacturacionElectronicaId(@PathVariable Long chequeraFacturacionElectronicaId) {
        service.deleteByChequeraFacturacionElectronicaId(chequeraFacturacionElectronicaId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
     
}
