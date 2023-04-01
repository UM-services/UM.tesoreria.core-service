package ar.edu.um.tesoreria.rest.controller;

import ar.edu.um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import ar.edu.um.tesoreria.rest.service.FacturacionElectronicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/facturacionElectronica")
public class FacturacionElectronicaController {

    @Autowired
    private FacturacionElectronicaService service;

    @GetMapping("/{facturacionElectronicaId}")
    public ResponseEntity<FacturacionElectronica> findByFacturacionElectronicaId(@PathVariable Long facturacionElectronicaId) {
        return new ResponseEntity<FacturacionElectronica>(service.findByFacturacionElectronicaId(facturacionElectronicaId), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<FacturacionElectronica> add(@RequestBody FacturacionElectronica facturacionElectronica) {
        return new ResponseEntity<FacturacionElectronica>(service.add(facturacionElectronica), HttpStatus.OK);
    }

}
