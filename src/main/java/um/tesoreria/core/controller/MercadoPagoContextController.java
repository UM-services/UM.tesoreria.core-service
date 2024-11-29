package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.service.MercadoPagoContextService;

@RestController
@RequestMapping("/api/tesoreria/core/mercadoPagoContext")
public class MercadoPagoContextController {

    private final MercadoPagoContextService service;

    public MercadoPagoContextController(MercadoPagoContextService service) {
        this.service = service;
    }

    @GetMapping("/cuota/activo/{chequeraCuotaId}")
    public ResponseEntity<MercadoPagoContext> findActivoByChequeraCuotaId(@PathVariable Long chequeraCuotaId) {
        try {
            return ResponseEntity.ok(service.findActiveByChequeraCuotaId(chequeraCuotaId));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
