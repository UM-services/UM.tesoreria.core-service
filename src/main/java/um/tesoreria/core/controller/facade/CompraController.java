package um.tesoreria.core.controller.facade;

import lombok.extern.slf4j.Slf4j;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.configuration.security.RequierePermisoInterceptor;
import um.tesoreria.core.exception.ComprobanteAnulacionException;
import um.tesoreria.core.exception.PermisoInsuficienteException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.kotlin.model.ValorMovimiento;
import um.tesoreria.core.service.facade.CompraService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/compra", "/api/tesoreria/core/compra"})
@Slf4j
public class CompraController {

    private final CompraService service;

    public CompraController(CompraService service) {
        this.service = service;
    }

    @GetMapping("/deleteComprobante/{proveedorMovimientoId}")
    public ResponseEntity<Boolean> deleteComprobante(@PathVariable Long proveedorMovimientoId) {
        try {
            service.deleteComprobante(proveedorMovimientoId);
            log.debug("Anulated Comprobante -> {}", proveedorMovimientoId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /**
     * Comando de anulación del módulo Pagos: revalida en el servidor las reglas de
     * negocio (ya anulada, costos asignados, privilegio de orden de pago) y ejecuta
     * la misma cascada de {@code deleteComprobante}.
     */
    @DeleteMapping("/comprobante/{proveedorMovimientoId}")
    @RequierePermiso("pagos.comprobantes.anular")
    public ResponseEntity<Map<String, Object>> anularComprobante(
            @PathVariable Long proveedorMovimientoId,
            HttpServletRequest request) {
        Long userId = resolveUserId(request);
        try {
            service.anularComprobante(proveedorMovimientoId, userId);
            log.debug("Anulated Comprobante (comando Pagos) -> {}", proveedorMovimientoId);
            return ResponseEntity.ok(Map.of("anulado", true));
        } catch (ComprobanteAnulacionException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("motivos", e.getMotivos()));
        } catch (PermisoInsuficienteException e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, e.getMessage());
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (Exception e) {
            log.debug("ERROR Anulating Comprobante -> {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    /** Identidad transitoria hasta que exista JWT (mismo header que el interceptor de permisos). */
    private Long resolveUserId(HttpServletRequest request) {
        String header = request.getHeader(RequierePermisoInterceptor.USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @GetMapping("/anulateValor/{valorMovimientoId}/{fechaContableAnulacion}")
    public ResponseEntity<Boolean> anulateValor(@PathVariable Long valorMovimientoId, @PathVariable @DateTimeFormat(iso = ISO.DATE_TIME) OffsetDateTime fechaContableAnulacion) {
        try {
            service.anulateValor(valorMovimientoId, fechaContableAnulacion);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/deleteValor/{valorMovimientoId}")
    public ResponseEntity<Boolean> deleteValor(@PathVariable Long valorMovimientoId) {
        try {
            service.deleteValor(valorMovimientoId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @PostMapping("/addValores/{proveedorMovimientoId}")
    public ResponseEntity<Boolean> addValores(@RequestBody List<ValorMovimiento> valorMovimientos, @PathVariable Long proveedorMovimientoId) {
        try {
            service.addValores(proveedorMovimientoId, valorMovimientos);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @GetMapping("/anulateOPago/{proveedorMovimientoId}")
    public ResponseEntity<Boolean> anulateOPago(@PathVariable Long proveedorMovimientoId) {
        try {
            service.anulateOPago(proveedorMovimientoId);
            log.debug("Anulated OrdenPago -> {}", proveedorMovimientoId);
            return ResponseEntity.ok(true);
        } catch (Exception e) {
            log.debug("ERROR Anulating OrdenPago -> {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE);
        }
    }

}
