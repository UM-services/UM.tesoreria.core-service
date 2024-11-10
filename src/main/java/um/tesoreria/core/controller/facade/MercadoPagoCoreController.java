package um.tesoreria.core.controller.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.kotlin.model.MercadoPagoContext;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.service.facade.MercadoPagoCoreService;

@RestController
@RequestMapping("/api/core/mercadopago")
public class MercadoPagoCoreController {

    private final MercadoPagoCoreService service;

    public MercadoPagoCoreController(MercadoPagoCoreService service) {
        this.service = service;
    }

    @GetMapping("/makeContext/{chequeraCuotaId}")
    public ResponseEntity<UMPreferenceMPDto> makeContext(@PathVariable Long chequeraCuotaId) {
        return ResponseEntity.ok(service.makeContext(chequeraCuotaId));
    }

    @PutMapping("/updateContext/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContext> updateContext(@PathVariable Long mercadoPagoContextId, @RequestBody MercadoPagoContext mercadoPagoContext) {
        return ResponseEntity.ok(service.updateContext(mercadoPagoContext, mercadoPagoContextId));
    }

    @GetMapping("/find/context/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContext> findContextByMercadoPagoContextId(@PathVariable Long mercadoPagoContextId) {
        return ResponseEntity.ok(service.findContextByMercadoPagoContextId(mercadoPagoContextId));
    }

}
