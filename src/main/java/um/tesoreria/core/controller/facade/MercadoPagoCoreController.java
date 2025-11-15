package um.tesoreria.core.controller.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.kotlin.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.model.MercadoPagoContext;
import um.tesoreria.core.service.facade.MercadoPagoCoreService;

@RestController
@RequestMapping("/api/tesoreria/core/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoCoreController {

    private final MercadoPagoCoreService service;

    @GetMapping("/makeContext/{chequeraCuotaId}")
    public ResponseEntity<UMPreferenceMPDto> makeContext(@PathVariable Long chequeraCuotaId) {
        try {
            return ResponseEntity.ok(service.makeContext(chequeraCuotaId));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/updateContext/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContext> updateContext(@RequestBody MercadoPagoContext mercadoPagoContext, @PathVariable Long mercadoPagoContextId) {
        return ResponseEntity.ok(service.updateContext(mercadoPagoContext, mercadoPagoContextId));
    }

    @GetMapping("/find/context/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContext> findContextByMercadoPagoContextId(@PathVariable Long mercadoPagoContextId) {
        try {
            return ResponseEntity.ok(service.findContextByMercadoPagoContextId(mercadoPagoContextId));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
