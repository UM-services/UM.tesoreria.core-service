package um.tesoreria.core.controller.facade;

import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.facade.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/compra")
public class CompraController {

    @Autowired
    private CompraService service;

    @GetMapping("/deleteComprobante/{proveedorMovimientoId}")
    public ResponseEntity<Boolean> deleteComprobante(@PathVariable Long proveedorMovimientoId) {
        try {
            service.deleteComprobante(proveedorMovimientoId);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/anulateValor/{valorMovimientoId}/{fechaContableAnulacion}")
    public ResponseEntity<Boolean> anulateValor(@PathVariable Long valorMovimientoId, @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fechaContableAnulacion) {
        try {
            service.anulateValor(valorMovimientoId, fechaContableAnulacion);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/deleteValor/{valorMovimientoId}")
    public ResponseEntity<Boolean> deleteValor(@PathVariable Long valorMovimientoId) {
        try {
            service.deleteValor(valorMovimientoId);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/addValores/{proveedorMovimientoId}")
    public ResponseEntity<Boolean> addValores(@RequestBody List<ValorMovimiento> valorMovimientos, @PathVariable Long proveedorMovimientoId) {
        try {
            service.addValores(proveedorMovimientoId, valorMovimientos);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/anulateOPago/{proveedorMovimientoId}")
    public ResponseEntity<Boolean> anulateOPago(@PathVariable Long proveedorMovimientoId) {
        try {
            service.anulateOPago(proveedorMovimientoId);
            return new ResponseEntity<Boolean>(true, HttpStatus.OK);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
