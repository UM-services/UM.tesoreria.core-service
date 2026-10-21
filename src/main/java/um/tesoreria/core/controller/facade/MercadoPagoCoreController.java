package um.tesoreria.core.controller.facade;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.PreferenciaPatch;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.SanitizacionContexto;
import um.tesoreria.core.model.dto.PreferenciaContextPatchDto;
import um.tesoreria.core.model.dto.RenovacionesCandidatosRequest;
import um.tesoreria.core.model.dto.SanitizacionContextosRequest;
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

    /**
     * Actualiza solo los datos de la preferencia (vencimiento, importe, link) sin tocar datos de pago.
     */
    @PatchMapping("/updateContext/{mercadoPagoContextId}/preferencia")
    public ResponseEntity<Void> updatePreferencia(@RequestBody PreferenciaContextPatchDto patch,
                                                  @PathVariable Long mercadoPagoContextId) {
        service.updatePreferencia(toDomain(patch), mercadoPagoContextId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Candidatos a renovar: contextos activos cuyo vencimiento publicado cayo en la ventana
     * y la cuota tiene un vencimiento posterior para publicar.
     */
    @PostMapping("/renovaciones")
    public ResponseEntity<List<UMPreferenceMPDto>> findRenovaciones(
            @RequestBody(required = false) RenovacionesCandidatosRequest request) {
        var pedido = request != null ? request : new RenovacionesCandidatosRequest();
        var referencia = pedido.getHoy() != null
                ? pedido.getHoy()
                : LocalDate.now().atStartOfDay().atOffset(ZoneOffset.UTC);
        var lookback = pedido.getLookbackDias() != null ? Math.max(0, pedido.getLookbackDias()) : 3;
        var reintentos = Boolean.TRUE.equals(pedido.getReintentos());
        var pagina = Math.max(1, Math.min(pedido.getSize() != null ? pedido.getSize() : 200, 500));
        return ResponseEntity.ok(service.findRenovaciones(referencia, lookback, reintentos,
                pedido.getAfterFecha(), pedido.getAfterId(), pagina));
    }

    /**
     * Sanitiza contextos activos duplicados por cuota: conserva el ultimo y desactiva el resto.
     */
    @PostMapping("/sanitize/activeContexts")
    public ResponseEntity<List<SanitizacionContexto>> sanitizeActiveContexts(
            @RequestBody(required = false) SanitizacionContextosRequest request) {
        var pedido = request != null ? request : new SanitizacionContextosRequest();
        var dryRun = pedido.getDryRun() == null || pedido.getDryRun();
        return ResponseEntity.ok(service.sanitizeActiveContexts(dryRun));
    }

    @GetMapping("/find/context/{mercadoPagoContextId}")
    public ResponseEntity<MercadoPagoContext> findContextByMercadoPagoContextId(@PathVariable Long mercadoPagoContextId) {
        try {
            return ResponseEntity.ok(service.findContextByMercadoPagoContextId(mercadoPagoContextId));
        } catch (MercadoPagoContextException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private PreferenciaPatch toDomain(PreferenciaContextPatchDto patch) {
        if (patch == null) {
            return null;
        }
        return new PreferenciaPatch(
                patch.getFechaVencimiento(),
                patch.getImporte(),
                patch.getInitPoint(),
                patch.getPreferenceId(),
                patch.getPreference(),
                patch.getChanged(),
                patch.getLastVencimientoUpdated());
    }

}
