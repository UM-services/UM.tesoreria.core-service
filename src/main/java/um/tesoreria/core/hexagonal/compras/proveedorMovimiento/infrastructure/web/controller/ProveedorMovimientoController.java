package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoRequest;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.mapper.ProveedorMovimientoDtoMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/proveedorMovimiento")
@RequiredArgsConstructor
public class ProveedorMovimientoController {

    private final ProveedorMovimientoService service;
    private final ProveedorMovimientoDtoMapper proveedorMovimientoDtoMapper;

    @GetMapping("/eliminables/{ejercicioId}")
    public ResponseEntity<List<ProveedorMovimientoResponse>> findAllEliminables(@PathVariable Integer ejercicioId) {
        List<ProveedorMovimientoResponse> responses = service.findAllEliminables(ejercicioId).stream()
                .map(proveedorMovimientoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/asignables/{proveedorId}/{desde}/{hasta}/{geograficaId}/{todos}")
    public ResponseEntity<List<ProveedorMovimientoResponse>> findAllAsignables(
            @PathVariable Integer proveedorId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime desde,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime hasta,
            @PathVariable Integer geograficaId,
            @PathVariable Boolean todos) {
        List<ProveedorMovimientoResponse> responses = service.findAllAsignables(proveedorId, desde, hasta, geograficaId, todos).stream()
                .map(proveedorMovimientoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{proveedorMovimientoId}")
    public ResponseEntity<ProveedorMovimientoResponse> findByProveedorMovimientoId(@PathVariable Long proveedorMovimientoId) {
        try {
            var domain = service.findByProveedorMovimientoId(proveedorMovimientoId);
            return ResponseEntity.ok(proveedorMovimientoDtoMapper.toResponse(domain));
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/ordenPago/{prefijo}/{numeroComprobante}")
    public ResponseEntity<ProveedorMovimientoResponse> findByOrdenPago(@PathVariable Integer prefijo,
                                                                        @PathVariable Long numeroComprobante) {
        try {
            var domain = service.findByOrdenPago(prefijo, numeroComprobante);
            return ResponseEntity.ok(proveedorMovimientoDtoMapper.toResponse(domain));
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/lastOrdenPago/{prefijo}")
    public ResponseEntity<ProveedorMovimientoResponse> findLastOrdenPago(@PathVariable Integer prefijo) {
        try {
            var domain = service.findLastOrdenPago(prefijo);
            return ResponseEntity.ok(proveedorMovimientoDtoMapper.toResponse(domain));
        } catch (ProveedorMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ProveedorMovimientoResponse> add(@RequestBody ProveedorMovimientoRequest proveedorMovimientoRequest) {
        ProveedorMovimiento domain = proveedorMovimientoDtoMapper.toDomain(proveedorMovimientoRequest);
        ProveedorMovimiento created = service.createProveedorMovimiento(domain);
        return ResponseEntity.ok(proveedorMovimientoDtoMapper.toResponse(created));
    }
}
