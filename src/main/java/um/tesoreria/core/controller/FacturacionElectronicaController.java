package um.tesoreria.core.controller;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.FacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.kotlin.model.FacturacionElectronica;
import um.tesoreria.core.service.ChequeraCuotaService;
import um.tesoreria.core.service.ChequeraPagoService;
import um.tesoreria.core.service.FacturacionElectronicaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/facturacionElectronica")
public class FacturacionElectronicaController {

    private final FacturacionElectronicaService service;
    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraCuotaService chequeraCuotaService;

    public FacturacionElectronicaController(FacturacionElectronicaService service, ChequeraPagoService chequeraPagoService, ChequeraCuotaService chequeraCuotaService) {
        this.service = service;
        this.chequeraPagoService = chequeraPagoService;
        this.chequeraCuotaService = chequeraCuotaService;
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<FacturacionElectronica>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        List<Long> chequeraPagoIds = chequeraPagoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, chequeraCuotaService).stream().map(ChequeraPago::getChequeraPagoId).collect(Collectors.toList());
        return new ResponseEntity<>(service.findAllByChequeraPagoIds(chequeraPagoIds), HttpStatus.OK);
    }

    @GetMapping("/{facturacionElectronicaId}")
    public ResponseEntity<FacturacionElectronica> findByFacturacionElectronicaId(@PathVariable Long facturacionElectronicaId) {
        try {
            return new ResponseEntity<>(service.findByFacturacionElectronicaId(facturacionElectronicaId), HttpStatus.OK);
        } catch (FacturacionElectronicaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/pago/{chequeraPagoId}")
    public ResponseEntity<FacturacionElectronica> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
        try {
            return new ResponseEntity<>(service.findByChequeraPagoId(chequeraPagoId), HttpStatus.OK);
        } catch (FacturacionElectronicaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<FacturacionElectronica>> findAllByPeriodo(@RequestParam OffsetDateTime fechaDesde, @RequestParam OffsetDateTime fechaHasta) {
        return new ResponseEntity<>(service.findAllByPeriodo(fechaDesde, fechaHasta), HttpStatus.OK);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<FacturacionElectronica>> find3Pendientes() {
        return new ResponseEntity<>(service.find3Pendientes(), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<FacturacionElectronica> add(@RequestBody FacturacionElectronica facturacionElectronica) {
        return new ResponseEntity<>(service.add(facturacionElectronica), HttpStatus.OK);
    }

    @PutMapping("/{facturacionElectronicaId}")
    public ResponseEntity<FacturacionElectronica> update(@RequestBody FacturacionElectronica facturacionElectronica, @PathVariable Long facturacionElectronicaId) {
        return new ResponseEntity<>(service.update(facturacionElectronica, facturacionElectronicaId), HttpStatus.OK);
    }

}
