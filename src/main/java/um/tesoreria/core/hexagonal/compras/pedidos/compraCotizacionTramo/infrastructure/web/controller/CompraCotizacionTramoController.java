package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.service.CompraCotizacionTramoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionMinimoResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.mapper.CompraCotizacionTramoDtoMapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * Tramos de cotizaciones mínimas por monto (tope en múltiplos de la referencia del ejercicio;
 * nulo = tramo sin tope).
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraCotizacionTramo")
@RequiredArgsConstructor
public class CompraCotizacionTramoController {

    private final CompraCotizacionTramoService compraCotizacionTramoService;
    private final CompraCotizacionTramoDtoMapper compraCotizacionTramoDtoMapper;

    @GetMapping
    public ResponseEntity<List<CompraCotizacionTramoResponse>> findAll() {
        List<CompraCotizacionTramoResponse> responses = compraCotizacionTramoService.findAll().stream()
                .map(compraCotizacionTramoDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{cotizacionTramoId}")
    public ResponseEntity<CompraCotizacionTramoResponse> findById(@PathVariable Long cotizacionTramoId) {
        return compraCotizacionTramoService.findById(cotizacionTramoId)
                .map(compraCotizacionTramoDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Tramo de cotizaciones no encontrado: " + cotizacionTramoId));
    }

    @GetMapping("/minimo")
    public ResponseEntity<CompraCotizacionMinimoResponse> minimo(@RequestParam Integer ejercicioId,
                                                                 @RequestParam BigDecimal monto) {
        return compraCotizacionTramoService.resolverMinimo(ejercicioId, monto)
                .map(minimo -> ResponseEntity.ok(new CompraCotizacionMinimoResponse(minimo)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "COTIZACIONES_NO_CONFIGURADAS"));
    }

    @PostMapping
    public ResponseEntity<CompraCotizacionTramoResponse> add(@Valid @RequestBody CompraCotizacionTramoRequest request) {
        try {
            CompraCotizacionTramo created = compraCotizacionTramoService.add(
                    compraCotizacionTramoDtoMapper.toDomain(request));
            return ResponseEntity.ok(compraCotizacionTramoDtoMapper.toResponse(created));
        } catch (CompraCotizacionTramoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{cotizacionTramoId}")
    public ResponseEntity<CompraCotizacionTramoResponse> update(@Valid @RequestBody CompraCotizacionTramoRequest request,
                                                                @PathVariable Long cotizacionTramoId) {
        try {
            CompraCotizacionTramo updated = compraCotizacionTramoService.update(
                    compraCotizacionTramoDtoMapper.toDomain(request), cotizacionTramoId);
            return ResponseEntity.ok(compraCotizacionTramoDtoMapper.toResponse(updated));
        } catch (CompraCotizacionTramoException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{cotizacionTramoId}")
    public ResponseEntity<Void> delete(@PathVariable Long cotizacionTramoId) {
        try {
            compraCotizacionTramoService.delete(cotizacionTramoId);
            return ResponseEntity.noContent().build();
        } catch (CompraCotizacionTramoException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
