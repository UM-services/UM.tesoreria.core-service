package um.tesoreria.core.controller.facade;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.model.dto.UMPreferenceMPDto;
import um.tesoreria.core.service.facade.MercadoPagoCoreService;

@RestController
@RequestMapping("/api/tesoreria/core/mercadopago")
@RequiredArgsConstructor
public class MercadoPagoCoreController {

    private final MercadoPagoCoreService service;

    @GetMapping("/makeContext/{chequeraCuotaId}")
    public ResponseEntity<UMPreferenceMPDto> makeContext(@PathVariable Long chequeraCuotaId) {
        try {
            return ResponseEntity.ok(service.makeContextCuota(chequeraCuotaId));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/makeContextsChequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<UMPreferenceMPDto>> makeContextsChequera(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId, @PathVariable Integer alternativaId) {
        try {
            return ResponseEntity.ok(service.makeContextsChequera(facultadId, tipoChequeraId, chequeraSerieId, alternativaId));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/updateContext/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContext> updateContext(@RequestBody MercadoPagoContext mercadoPagoContext, @PathVariable Long mercadoPagoContextId) {
        return ResponseEntity.ok(service.updateContext(mercadoPagoContext, mercadoPagoContextId));
    }

    @PutMapping("/updateContexts")
    public ResponseEntity<List<MercadoPagoContext>> updateContexts(@RequestBody List<MercadoPagoContext> contexts) {
        return ResponseEntity.ok(service.updateContexts(contexts));
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
