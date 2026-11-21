package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.TrazaContableService;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.ReconstruccionPlan;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.ReconstruccionPlanResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.ReconstruccionRequest;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.mapper.TrazaContableDtoMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Consulta y reconstrucción de la traza contable de una orden de pago. El GET es el dry-run
 * (plan de cambios) y el POST aplica ese plan.
 */
@RestController
@RequestMapping({"/pagos/trazaContable", "/api/tesoreria/core/pagos/trazaContable"})
@RequiredArgsConstructor
public class TrazaContableController {

    private final TrazaContableService trazaContableService;
    private final TrazaContableDtoMapper trazaContableDtoMapper;

    @GetMapping("/{proveedorMovimientoId}")
    @RequierePermiso("pagos.traza.consultar")
    public ResponseEntity<ReconstruccionPlanResponse> preview(@PathVariable Long proveedorMovimientoId) {
        ReconstruccionPlan plan = trazaContableService.preview(proveedorMovimientoId);
        return ResponseEntity.ok(trazaContableDtoMapper.toResponse(plan));
    }

    @PostMapping("/{proveedorMovimientoId}/reconstruir")
    @RequierePermiso("pagos.traza.reconstruir")
    public ResponseEntity<ReconstruccionPlanResponse> reconstruir(@PathVariable Long proveedorMovimientoId,
                                                                  @RequestBody(required = false) ReconstruccionRequest request) {
        boolean forzar = request != null && Boolean.TRUE.equals(request.forzar());
        ReconstruccionPlan plan = trazaContableService.reconstruir(proveedorMovimientoId, forzar);
        return ResponseEntity.ok(trazaContableDtoMapper.toResponse(plan));
    }

    @ExceptionHandler(OrdenPagoValidacionException.class)
    public ResponseEntity<Map<String, Object>> validacion(OrdenPagoValidacionException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 422);
        body.put("error", "VALIDACION");
        body.put("message", exception.getMessage());
        body.put("errores", exception.getErrores());
        return ResponseEntity.unprocessableEntity().body(body);
    }

    @ExceptionHandler(OrdenPagoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(OrdenPagoException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 404);
        body.put("error", "NO_ENCONTRADO");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(404).body(body);
    }

}
