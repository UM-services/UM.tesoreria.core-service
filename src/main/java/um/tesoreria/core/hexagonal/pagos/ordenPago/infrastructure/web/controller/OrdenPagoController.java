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
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedor.application.service.ProveedorService;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoService;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.out.NotificacionOrdenPagoPort;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDetalleResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoPreviewResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoRequest;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.mapper.OrdenPagoDtoMapper;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping({"/pagos/ordenPago", "/api/tesoreria/core/pagos/ordenPago"})
@RequiredArgsConstructor
public class OrdenPagoController {

    private final OrdenPagoService ordenPagoService;
    private final OrdenPagoDtoMapper mapper;
    private final ProveedorService proveedorService;
    private final NotificacionOrdenPagoPort notificacionOrdenPagoPort;

    @ExceptionHandler(OrdenPagoValidacionException.class)
    public ResponseEntity<Map<String, Object>> validacion(OrdenPagoValidacionException ex) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", 422);
        body.put("error", "VALIDACION");
        body.put("message", ex.getMessage());
        body.put("errores", ex.getErrores());
        return ResponseEntity.unprocessableEntity().body(body);
    }

    @ExceptionHandler({OrdenPagoException.class, ProveedorMovimientoException.class})
    public ResponseEntity<Map<String, Object>> noEncontrado(RuntimeException ex) {
        return ResponseEntity.status(404).body(Map.of(
                "status", 404,
                "error", "NO_ENCONTRADO",
                "message", String.valueOf(ex.getMessage())));
    }

    @PostMapping("/preview")
    @RequierePermiso("pagos.orden_pago.crear")
    public ResponseEntity<OrdenPagoPreviewResponse> preview(@RequestBody OrdenPagoRequest request) {
        return ResponseEntity.ok(mapper.toResponse(ordenPagoService.preview(mapper.toDomain(request))));
    }

    @PostMapping
    @RequierePermiso("pagos.orden_pago.crear")
    public ResponseEntity<OrdenPagoResponse> crear(@RequestBody OrdenPagoRequest request) {
        OrdenPagoResultado resultado = ordenPagoService.create(mapper.toDomain(request));
        if (Boolean.TRUE.equals(request.notificar())) {
            actualizarCorreos(request);
            notificacionOrdenPagoPort.notificar(resultado.proveedorMovimientoId());
        }
        return ResponseEntity.ok(mapper.toResponse(resultado));
    }

    @GetMapping("/{proveedorMovimientoId}")
    @RequierePermiso("pagos.orden_pago.consultar")
    public ResponseEntity<OrdenPagoDetalleResponse> consultar(@PathVariable Long proveedorMovimientoId) {
        return ResponseEntity.ok(mapper.toResponse(ordenPagoService.findDetalle(proveedorMovimientoId)));
    }

    /**
     * El formulario legacy actualiza los correos del proveedor cuando se marca "Notificar".
     * Se hace fuera de la transacción del alta, igual que el formulario original.
     */
    private void actualizarCorreos(OrdenPagoRequest request) {
        if (request.proveedorId() == null) {
            return;
        }
        proveedorService.getByProveedorId(request.proveedorId()).ifPresent(proveedor -> {
            boolean cambio = false;
            if (request.emailProveedor() != null && !request.emailProveedor().isBlank()
                    && !request.emailProveedor().equals(proveedor.getEmail())) {
                proveedor.setEmail(request.emailProveedor());
                cambio = true;
            }
            if (request.emailInterno() != null && !request.emailInterno().isBlank()
                    && !request.emailInterno().equals(proveedor.getEmailInterno())) {
                proveedor.setEmailInterno(request.emailInterno());
                cambio = true;
            }
            if (cambio) {
                proveedorService.update(proveedor.getProveedorId(), proveedor);
            }
        });
    }
}
