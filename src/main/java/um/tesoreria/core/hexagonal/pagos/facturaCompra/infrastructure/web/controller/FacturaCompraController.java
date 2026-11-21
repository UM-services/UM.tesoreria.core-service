package um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.pagos.facturaCompra.application.exception.FacturaCompraException;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.ports.in.FacturaCompraUseCase;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraDetalleResponse;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraRequest;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.dto.FacturaCompraResponse;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.infrastructure.web.mapper.FacturaCompraDtoMapper;

import java.util.Map;

@RestController
@RequestMapping({"/pagos/factura", "/api/tesoreria/core/pagos/factura"})
@RequiredArgsConstructor
public class FacturaCompraController {

    private final FacturaCompraUseCase service;
    private final FacturaCompraDtoMapper mapper;

    @ExceptionHandler(FacturaCompraException.class)
    public ResponseEntity<Map<String, Object>> error(FacturaCompraException ex) {
        return ResponseEntity.status(ex.getCodigo().status()).body(Map.of(
                "status", ex.getCodigo().status().value(),
                "error", ex.getCodigo().name(),
                "message", ex.getMessage()));
    }

    @PostMapping
    @RequierePermiso("pagos.factura.crear")
    public ResponseEntity<FacturaCompraResponse> registrar(@RequestBody FacturaCompraRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.registrar(mapper.toDomain(request))));
    }

    @GetMapping("/{proveedorMovimientoId}")
    @RequierePermiso("pagos.factura.consultar")
    public ResponseEntity<FacturaCompraDetalleResponse> consultar(@PathVariable Long proveedorMovimientoId) {
        return ResponseEntity.ok(mapper.toResponse(service.consultar(proveedorMovimientoId)));
    }

    @GetMapping("/existe/{proveedorId}/{comprobanteId}/{prefijo}/{numeroComprobante}")
    @RequierePermiso("pagos.factura.consultar")
    public ResponseEntity<Boolean> existe(@PathVariable Integer proveedorId,
                                          @PathVariable Integer comprobanteId,
                                          @PathVariable Integer prefijo,
                                          @PathVariable Long numeroComprobante) {
        return ResponseEntity.ok(service.existe(proveedorId, comprobanteId, prefijo, numeroComprobante));
    }

}
