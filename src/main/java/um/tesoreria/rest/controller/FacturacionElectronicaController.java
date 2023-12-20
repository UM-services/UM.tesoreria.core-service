package um.tesoreria.rest.controller;

import um.tesoreria.rest.kotlin.model.FacturacionElectronica;
import um.tesoreria.rest.service.ChequeraCuotaService;
import um.tesoreria.rest.service.ChequeraPagoService;
import um.tesoreria.rest.service.FacturacionElectronicaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/facturacionElectronica")
public class FacturacionElectronicaController {

    private final FacturacionElectronicaService service;

    private final ChequeraPagoService chequeraPagoService;

    private final ChequeraCuotaService chequeraCuotaService;

    @Autowired
    public FacturacionElectronicaController(FacturacionElectronicaService service, ChequeraPagoService chequeraPagoService, ChequeraCuotaService chequeraCuotaService) {
        this.service = service;
        this.chequeraPagoService = chequeraPagoService;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<FacturacionElectronica>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        List<Long> chequeraPagoIds = chequeraPagoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, chequeraCuotaService).stream().map(chequeraPago -> chequeraPago.getChequeraPagoId()).collect(Collectors.toList());
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
