package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception.OrdenPagoValidacionException;
import um.tesoreria.core.hexagonal.pagos.ordenPago.application.service.OrdenPagoService;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDirectaSolicitud;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoPreview;
import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoResultado;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoDirectaRequest;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoPreviewResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto.OrdenPagoResponse;
import um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.mapper.OrdenPagoDtoMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/** Orden de pago directa: egreso sin proveedor ni facturas, con gastos y valores. */
@RestController
@RequestMapping({"/pagos/ordenPagoDirecta", "/api/tesoreria/core/pagos/ordenPagoDirecta"})
@RequiredArgsConstructor
public class OrdenPagoDirectaController {

    private final OrdenPagoService ordenPagoService;
    private final OrdenPagoDtoMapper ordenPagoDtoMapper;

    @PostMapping("/preview")
    @RequierePermiso("pagos.orden_pago.crear")
    public ResponseEntity<OrdenPagoPreviewResponse> preview(@RequestBody OrdenPagoDirectaRequest request) {
        OrdenPagoDirectaSolicitud solicitud = ordenPagoDtoMapper.toDomain(request);
        OrdenPagoPreview preview = ordenPagoService.previewDirecta(solicitud);
        return ResponseEntity.ok(ordenPagoDtoMapper.toResponse(preview));
    }

    @PostMapping
    @RequierePermiso("pagos.orden_pago.crear")
    public ResponseEntity<OrdenPagoResponse> create(@RequestBody OrdenPagoDirectaRequest request) {
        OrdenPagoDirectaSolicitud solicitud = ordenPagoDtoMapper.toDomain(request);
        OrdenPagoResultado resultado = ordenPagoService.createDirecta(solicitud);
        return ResponseEntity.ok(ordenPagoDtoMapper.toResponse(resultado));
    }

    @ExceptionHandler(OrdenPagoValidacionException.class)
    public ResponseEntity<Map<String, Object>> validacion(OrdenPagoValidacionException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.UNPROCESSABLE_ENTITY.value());
        body.put("error", "VALIDACION");
        body.put("message", exception.getMessage());
        body.put("errores", exception.getErrores());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(body);
    }

    @ExceptionHandler(OrdenPagoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(OrdenPagoException exception) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("error", "NO_ENCONTRADO");
        body.put("message", exception.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }
}
