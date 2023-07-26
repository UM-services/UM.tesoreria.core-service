/**
 *
 */
package um.tesoreria.rest.controller;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.*;
import um.tesoreria.rest.kotlin.model.ProveedorMovimiento;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import um.tesoreria.rest.exception.ProveedorMovimientoException;
import um.tesoreria.rest.service.ProveedorMovimientoService;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping("/proveedorMovimiento")
public class ProveedorMovimientoController {

    @Autowired
    private ProveedorMovimientoService service;

    @GetMapping("/eliminables/{ejercicioId}")
    public ResponseEntity<List<ProveedorMovimiento>> findAllEliminables(@PathVariable Integer ejercicioId) {
        return new ResponseEntity<>(service.findAllEliminables(ejercicioId), HttpStatus.OK);
    }

    @GetMapping("/asignables/{proveedorId}/{desde}/{hasta}/{geograficaId}/{todos}")
    public ResponseEntity<List<ProveedorMovimiento>> findAllAsignables(@PathVariable Integer proveedorId,
                                                                       @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
                                                                       @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
                                                                       @PathVariable Integer geograficaId, @PathVariable Boolean todos) throws JsonProcessingException {
        return new ResponseEntity<>(
                service.findAllAsignables(proveedorId, desde, hasta, geograficaId, todos), HttpStatus.OK);
    }

    @GetMapping("/{proveedorMovimientoId}")
    public ResponseEntity<ProveedorMovimiento> findByProveedorMovimientoId(@PathVariable Long proveedorMovimientoId) {
        try {
            return new ResponseEntity<>(service.findByProveedorMovimientoId(proveedorMovimientoId),
                    HttpStatus.OK);
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/ordenPago/{prefijo}/{numeroComprobante}")
    public ResponseEntity<ProveedorMovimiento> findByOrdenPago(@PathVariable Integer prefijo, @PathVariable Long numeroComprobante) {
        try {
            return new ResponseEntity<>(service.findByOrdenPago(prefijo, numeroComprobante),
                    HttpStatus.OK);
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/lastOrdenPago/{prefijo}")
    public ResponseEntity<ProveedorMovimiento> findLastOrdenPago(@PathVariable Integer prefijo) {
        try {
            return new ResponseEntity<>(service.findLastOrdenPago(prefijo), HttpStatus.OK);
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ProveedorMovimiento> add(@RequestBody ProveedorMovimiento proveedorMovimiento) {
        return new ResponseEntity<>(service.add(proveedorMovimiento), HttpStatus.OK);
    }

}
