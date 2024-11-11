package um.tesoreria.core.controller.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.service.facade.PagoService;

@RestController
@RequestMapping("/api/core/pago")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping("/registra/pago/mercadopago/{mercadoPagoContextId}")
    public ResponseEntity<ChequeraPago> registrarPagoMercadoPago(@PathVariable Long mercadoPagoContextId) {
        return ResponseEntity.ok(service.registraPagoMP(mercadoPagoContextId));
    }
}
