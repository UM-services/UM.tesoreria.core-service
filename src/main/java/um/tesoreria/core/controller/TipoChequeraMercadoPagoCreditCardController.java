package um.tesoreria.core.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.TipoChequeraMercadoPagoCreditCardException;
import um.tesoreria.core.model.TipoChequeraMercadoPagoCreditCard;
import um.tesoreria.core.service.TipoChequeraMercadoPagoCreditCardService;

@RestController
@RequestMapping({ "/tipoChequeraMercadoPagoCreditCard", "/api/tesoreria/core/tipoChequeraMercadoPagoCreditCard"})
public class TipoChequeraMercadoPagoCreditCardController {

    private final TipoChequeraMercadoPagoCreditCardService service;

    public TipoChequeraMercadoPagoCreditCardController(TipoChequeraMercadoPagoCreditCardService service) {
        this.service = service;
    }

    @PostMapping("/persist/{tipoChequeraId}/{alternativaId}/{cuotas}")
    public ResponseEntity<String> persist(@PathVariable Integer tipoChequeraId, @PathVariable Integer alternativaId, @PathVariable Integer cuotas) {
        return ResponseEntity.ok(service.persist(tipoChequeraId, alternativaId, cuotas));
    }

    @DeleteMapping("/baja/{tipoChequeraId}")
    public ResponseEntity<String> baja(@PathVariable Integer tipoChequeraId) {
        return ResponseEntity.ok(service.baja(tipoChequeraId));
    }

    @GetMapping("/tipoChequera/{tipoChequeraId}")
    public ResponseEntity<TipoChequeraMercadoPagoCreditCard> findByTipoChequeraId(@PathVariable Integer tipoChequeraId) {
        try {
            return ResponseEntity.ok(service.findByTipoChequeraId(tipoChequeraId));
        } catch (TipoChequeraMercadoPagoCreditCardException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
