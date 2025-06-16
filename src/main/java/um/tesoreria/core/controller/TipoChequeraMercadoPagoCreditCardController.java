package um.tesoreria.core.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.TipoChequeraMercadoPagoCreditCardException;
import um.tesoreria.core.model.TipoChequeraMercadoPagoCreditCard;
import um.tesoreria.core.service.TipoChequeraMercadoPagoCreditCardService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping({ "/tipoChequeraMercadoPagoCreditCard", "/api/tesoreria/core/tipoChequeraMercadoPagoCreditCard"})
public class TipoChequeraMercadoPagoCreditCardController {

    private final TipoChequeraMercadoPagoCreditCardService service;

    public TipoChequeraMercadoPagoCreditCardController(TipoChequeraMercadoPagoCreditCardService service) {
        this.service = service;
    }

    @GetMapping("/activos")
    public ResponseEntity<List<TipoChequeraMercadoPagoCreditCard>> findAllActivos() {
        return ResponseEntity.ok(service.findAllActivos());
    }

    @PostMapping("/persist/{tipoChequeraId}/{alternativaId}/{cuotas}")
    public ResponseEntity<String> persist(@PathVariable Integer tipoChequeraId, @PathVariable Integer alternativaId, @PathVariable Integer cuotas) {
        return ResponseEntity.ok(service.persist(tipoChequeraId, alternativaId, cuotas));
    }

    @DeleteMapping("/baja/{tipoChequeraId}/{alternativaId}")
    public ResponseEntity<String> baja(@PathVariable Integer tipoChequeraId, @PathVariable Integer alternativaId) {
        return ResponseEntity.ok(service.baja(tipoChequeraId, alternativaId));
    }

    @DeleteMapping("/baja/id/{id}")
    public ResponseEntity<String> bajaId(@PathVariable UUID id) {
        return ResponseEntity.ok(service.bajaId(id));
    }

    @GetMapping("/unique/{tipoChequeraId}/{alternativaId}")
    public ResponseEntity<TipoChequeraMercadoPagoCreditCard> findByUnique(@PathVariable Integer tipoChequeraId, @PathVariable Integer alternativaId) {
        try {
            return ResponseEntity.ok(service.findByUnique(tipoChequeraId, alternativaId));
        } catch (TipoChequeraMercadoPagoCreditCardException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
