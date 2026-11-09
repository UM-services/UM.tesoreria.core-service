package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.exception.CompraFormalizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.application.service.CompraFormalizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionCriteriaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.mapper.CompraFormalizacionDtoMapper;

import java.util.List;

/**
 * Orden de Compra (etapa 8): instrumento emitido desde una adjudicación. Ruta canónica
 * {@code /api/tesoreria/core/compraFormalizacion}.
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraFormalizacion")
@RequiredArgsConstructor
public class CompraFormalizacionController {

    private final CompraFormalizacionService compraFormalizacionService;
    private final CompraFormalizacionDtoMapper compraFormalizacionDtoMapper;

    @GetMapping
    public ResponseEntity<List<CompraFormalizacionResponse>> findAll() {
        CompraFormalizacionCriteria criteria = new CompraFormalizacionCriteria(null, null, null, null);
        return ResponseEntity.ok(compraFormalizacionService.listar(criteria).stream()
                .map(compraFormalizacionDtoMapper::toResponse)
                .toList());
    }

    /** Adjudicaciones del ejercicio en curso pendientes de emitir la Orden de Compra. */
    @GetMapping("/pendientes")
    public ResponseEntity<List<CompraFormalizacionPendienteResponse>> pendientes() {
        return pendientes(null);
    }

    /** Adjudicaciones del ejercicio indicado pendientes de emitir la Orden de Compra. */
    @GetMapping("/pendientes/{ejercicioId}")
    public ResponseEntity<List<CompraFormalizacionPendienteResponse>> pendientes(
            @PathVariable Integer ejercicioId) {
        return ResponseEntity.ok(compraFormalizacionService.pendientes(ejercicioId).stream()
                .map(compraFormalizacionDtoMapper::toResponse)
                .toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<CompraFormalizacionResponse>> search(@RequestBody CompraFormalizacionCriteriaRequest request) {
        CompraFormalizacionCriteria criteria = new CompraFormalizacionCriteria(request.ejercicioId(),
                request.proveedorId(), request.fechaDesde(), request.fechaHasta());
        return ResponseEntity.ok(compraFormalizacionService.listar(criteria).stream()
                .map(compraFormalizacionDtoMapper::toResponse)
                .toList());
    }

    /** La OC emitida para un proceso de cotización, si existe. */
    @GetMapping("/por-cotizacion/{compraCotizacionId}")
    public ResponseEntity<CompraFormalizacionResponse> porCotizacion(@PathVariable Long compraCotizacionId) {
        return compraFormalizacionService.porCotizacion(compraCotizacionId)
                .map(compraFormalizacionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "El proceso " + compraCotizacionId + " no tiene Orden de Compra"));
    }

    @GetMapping("/{compraFormalizacionId}")
    public ResponseEntity<CompraFormalizacionResponse> findById(@PathVariable Long compraFormalizacionId) {
        return compraFormalizacionService.getById(compraFormalizacionId)
                .map(compraFormalizacionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Orden de Compra no encontrada: " + compraFormalizacionId));
    }

    /** Emite la Orden de Compra de una adjudicación. */
    @PostMapping
    public ResponseEntity<CompraFormalizacionResponse> formalizar(@RequestBody CompraFormalizacionRequest request) {
        if (request == null || request.getCompraCotizacionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "compraCotizacionId es requerido");
        }
        try {
            return ResponseEntity.ok(compraFormalizacionDtoMapper.toResponse(
                    compraFormalizacionService.formalizar(request.getCompraCotizacionId(),
                            compraFormalizacionDtoMapper.toDomain(request), request.getUsuarioId())));
        } catch (CompraFormalizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

}
