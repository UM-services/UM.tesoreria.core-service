package um.tesoreria.core.controller.facade;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.kotlin.model.ChequeraPago;
import um.tesoreria.core.kotlin.model.ChequeraPagoAsiento;
import um.tesoreria.core.model.dto.ItemAsientoDto;
import um.tesoreria.core.model.dto.PagoDto;
import um.tesoreria.core.service.facade.PagoService;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/pago")
public class PagoController {

    private final PagoService service;

    public PagoController(PagoService service) {
        this.service = service;
    }

    @GetMapping("/registra/pago/mercadopago/{mercadoPagoContextId}")
    public ResponseEntity<ChequeraPago> registrarPagoMercadoPago(@PathVariable Long mercadoPagoContextId) {
        return ResponseEntity.ok(service.registraPagoMP(mercadoPagoContextId));
    }

    @GetMapping("/pagos/{tipoPagoId}/{fecha}")
    public ResponseEntity<List<PagoDto>> getPagos(@PathVariable Integer tipoPagoId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime fecha) {
        return ResponseEntity.ok(service.getPagos(tipoPagoId, fecha));
    }

    @GetMapping("/items/{tipoPagoId}/{fecha}")
    public ResponseEntity<List<ItemAsientoDto>> getItems(@PathVariable Integer tipoPagoId, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) OffsetDateTime fecha) {
        return ResponseEntity.ok(service.getItems(tipoPagoId, fecha));
    }

}
