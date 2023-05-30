package um.tesoreria.rest.controller;

import um.tesoreria.rest.exception.ChequeraFacturacionElectronicaException;
import um.tesoreria.rest.kotlin.model.ChequeraFacturacionElectronica;
import um.tesoreria.rest.service.ChequeraFacturacionElectronicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/chequeraFacturacionElectronica")
public class ChequeraFacturacionElectronicaController {

    @Autowired
    private ChequeraFacturacionElectronicaService service;

    @GetMapping("/chequera/{chequeraId}")
    public ResponseEntity<ChequeraFacturacionElectronica> findByChequeraId(@PathVariable Long chequeraId) {
        try {
            return new ResponseEntity<ChequeraFacturacionElectronica>(service.findByChequeraId(chequeraId), HttpStatus.OK);
        } catch (ChequeraFacturacionElectronicaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraFacturacionElectronica> add(@RequestBody ChequeraFacturacionElectronica chequeraFacturacionElectronica) {
        return new ResponseEntity<ChequeraFacturacionElectronica>(service.add(chequeraFacturacionElectronica), HttpStatus.OK);
    }

    @PutMapping("/{chequeraFacturacionElectronicaId}")
    public ResponseEntity<ChequeraFacturacionElectronica> update(@RequestBody ChequeraFacturacionElectronica chequeraFacturacionElectronica, @PathVariable Long chequeraFacturacionElectronicaId) {
        return new ResponseEntity<ChequeraFacturacionElectronica>(service.update(chequeraFacturacionElectronica, chequeraFacturacionElectronicaId), HttpStatus.OK);
    }

    @DeleteMapping("/{chequeraFacturacionElectronicaId}")
    public ResponseEntity<Void> deleteByChequeraFacturacionElectronicaId(@PathVariable Long chequeraFacturacionElectronicaId) {
        service.deleteByChequeraFacturacionElectronicaId(chequeraFacturacionElectronicaId);
        return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
    }
}
