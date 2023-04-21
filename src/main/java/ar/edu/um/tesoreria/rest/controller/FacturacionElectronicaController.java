package ar.edu.um.tesoreria.rest.controller;

import ar.edu.um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import ar.edu.um.tesoreria.rest.service.ChequeraPagoService;
import ar.edu.um.tesoreria.rest.service.FacturacionElectronicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/facturacionElectronica")
public class FacturacionElectronicaController {

    @Autowired
    private FacturacionElectronicaService service;

    @Autowired
    private ChequeraPagoService chequeraPagoService;

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<FacturacionElectronica>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        List<Long> chequeraPagoIds = chequeraPagoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId).stream().map(chequeraPago -> chequeraPago.getChequeraPagoId()).collect(Collectors.toList());
        return new ResponseEntity<List<FacturacionElectronica>>(service.findAllByChequeraPagoIds(chequeraPagoIds), HttpStatus.OK);
    }

    @GetMapping("/{facturacionElectronicaId}")
    public ResponseEntity<FacturacionElectronica> findByFacturacionElectronicaId(@PathVariable Long facturacionElectronicaId) {
        return new ResponseEntity<FacturacionElectronica>(service.findByFacturacionElectronicaId(facturacionElectronicaId), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<FacturacionElectronica> add(@RequestBody FacturacionElectronica facturacionElectronica) {
        return new ResponseEntity<FacturacionElectronica>(service.add(facturacionElectronica), HttpStatus.OK);
    }

}
