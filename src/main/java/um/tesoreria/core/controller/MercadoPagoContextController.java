package um.tesoreria.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.kotlin.model.MercadoPagoContext;
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
        return ResponseEntity.ok(service.findActiveByChequeraCuotaId(chequeraCuotaId));
    }

}
