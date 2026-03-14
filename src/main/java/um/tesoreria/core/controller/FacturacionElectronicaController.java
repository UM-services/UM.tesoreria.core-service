package um.tesoreria.core.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.FacturacionElectronicaException;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.model.FacturacionElectronica;
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
@RequiredArgsConstructor
public class FacturacionElectronicaController {

    private final FacturacionElectronicaService service;
    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraCuotaService chequeraCuotaService;

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<FacturacionElectronica>> findAllByChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        List<Long> chequeraPagoIds = chequeraPagoService.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId, chequeraCuotaService).stream().map(ChequeraPago::getChequeraPagoId).collect(Collectors.toList());
        return ResponseEntity.ok(service.findAllByChequeraPagoIds(chequeraPagoIds));
    }

    @GetMapping("/{facturacionElectronicaId}")
    public ResponseEntity<FacturacionElectronica> findByFacturacionElectronicaId(@PathVariable Long facturacionElectronicaId) {
        try {
            return ResponseEntity.ok(service.findByFacturacionElectronicaId(facturacionElectronicaId));
        } catch (FacturacionElectronicaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/pago/{chequeraPagoId}")
    public ResponseEntity<FacturacionElectronica> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
        try {
            return ResponseEntity.ok(service.findByChequeraPagoId(chequeraPagoId));
        } catch (FacturacionElectronicaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/periodo")
    public ResponseEntity<List<FacturacionElectronica>> findAllByPeriodo(@RequestParam OffsetDateTime fechaDesde, @RequestParam OffsetDateTime fechaHasta) {
        return ResponseEntity.ok(service.findAllByPeriodo(fechaDesde, fechaHasta));
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<FacturacionElectronica>> find3Pendientes() {
        return ResponseEntity.ok(service.find3Pendientes());
    }

    @PostMapping("/")
    public ResponseEntity<FacturacionElectronica> add(@RequestBody FacturacionElectronica facturacionElectronica) {
        return ResponseEntity.ok(service.add(facturacionElectronica));
    }

    @PutMapping("/{facturacionElectronicaId}")
    public ResponseEntity<FacturacionElectronica> update(@RequestBody FacturacionElectronica facturacionElectronica, @PathVariable Long facturacionElectronicaId) {
        return ResponseEntity.ok(service.update(facturacionElectronica, facturacionElectronicaId));
    }

}
