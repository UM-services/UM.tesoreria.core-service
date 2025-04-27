package um.tesoreria.core.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

    @GetMapping("/tipoChequera/{tipoChequeraId}")
    public ResponseEntity<TipoChequeraMercadoPagoCreditCard> findByTipoChequeraId(@PathVariable Integer tipoChequeraId) {
        try {
            return ResponseEntity.ok(service.findByTipoChequeraId(tipoChequeraId));
        } catch (TipoChequeraMercadoPagoCreditCardException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
