package ar.edu.um.tesoreria.rest.controller.facade;

import ar.edu.um.tesoreria.rest.service.facade.CompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
}
