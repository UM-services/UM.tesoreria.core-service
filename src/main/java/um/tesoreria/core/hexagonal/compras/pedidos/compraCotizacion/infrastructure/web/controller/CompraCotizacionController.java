package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.service.CompraCotizacionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.AdjudicarCompraCotizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionCriteriaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraAdjudicacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionProveedorPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionProveedorResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionRespuestaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.RecomendarCompraCotizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.mapper.CompraCotizacionDtoMapper;

import java.util.List;

/**
 * Proceso de cotización (agrupador de ítems combinados + documentos por proveedor).
 * Ruta canónica {@code /api/tesoreria/core/compraCotizacion}.
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraCotizacion")
@RequiredArgsConstructor
public class CompraCotizacionController {

    private final CompraCotizacionService compraCotizacionService;
    private final CompraCotizacionDtoMapper compraCotizacionDtoMapper;

    @GetMapping
    public ResponseEntity<List<CompraCotizacionResponse>> findAll() {
        List<CompraCotizacionResponse> responses = compraCotizacionService
                .listar(new CompraCotizacionCriteria(null, null, null, null)).stream()
                .map(compraCotizacionDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/search")
    public ResponseEntity<List<CompraCotizacionResponse>> search(@RequestBody CompraCotizacionCriteriaRequest request) {
        CompraCotizacionCriteria criteria = new CompraCotizacionCriteria(request.estado(), request.ejercicioId(),
                request.fechaDesde(), request.fechaHasta());
        List<CompraCotizacionResponse> responses = compraCotizacionService.listar(criteria).stream()
                .map(compraCotizacionDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pendientes")
    public ResponseEntity<List<CompraCotizacionPendienteResponse>> pendientes(
            @RequestParam(required = false) Integer ejercicioId,
            @RequestParam(required = false) List<Integer> dependenciaIds) {
        return ResponseEntity.ok(compraCotizacionService.pendientes(ejercicioId, dependenciaIds).stream()
                .map(compraCotizacionDtoMapper::toResponse)
                .toList());
    }

    /** Bandeja transversal "Cotizaciones pedidas": documentos de proveedor en ENVIADA. */
    @GetMapping("/proveedores-pendientes")
    public ResponseEntity<List<CompraCotizacionProveedorPendienteResponse>> proveedoresPendientes(
            @RequestParam(required = false) Integer ejercicioId) {
        return ResponseEntity.ok(compraCotizacionService.respuestasPendientes(ejercicioId).stream()
                .map(compraCotizacionDtoMapper::toResponse)
                .toList());
    }

    @GetMapping("/{compraCotizacionId}")
    public ResponseEntity<CompraCotizacionResponse> findById(@PathVariable Long compraCotizacionId) {
        return compraCotizacionService.getById(compraCotizacionId)
                .map(compraCotizacionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Proceso de cotización no encontrado: " + compraCotizacionId));
    }

    @PostMapping
    public ResponseEntity<CompraCotizacionResponse> create(@RequestBody CompraCotizacionRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.crear(compraCotizacionDtoMapper.toDomain(request))));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{compraCotizacionId}")
    public ResponseEntity<CompraCotizacionResponse> update(@PathVariable Long compraCotizacionId,
                                                           @RequestBody CompraCotizacionRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.actualizar(compraCotizacionId, compraCotizacionDtoMapper.toDomain(request))));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraCotizacionId}/enviar")
    public ResponseEntity<CompraCotizacionResponse> enviar(@PathVariable Long compraCotizacionId) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.enviar(compraCotizacionId)));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraCotizacionId}/anular")
    public ResponseEntity<CompraCotizacionResponse> anular(@PathVariable Long compraCotizacionId,
                                                           @RequestBody(required = false) MotivoRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.anular(compraCotizacionId, request == null ? null : request.motivo())));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    // ---- Etapa 6C: respuestas, comparación y recomendación ----

    @PostMapping("/{compraCotizacionId}/proveedores/{compraCotizacionProveedorId}/respuesta")
    public ResponseEntity<CompraCotizacionProveedorResponse> registrarRespuesta(
            @PathVariable Long compraCotizacionId,
            @PathVariable Long compraCotizacionProveedorId,
            @RequestBody CompraCotizacionRespuestaRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.registrarRespuesta(compraCotizacionId, compraCotizacionProveedorId,
                            compraCotizacionDtoMapper.toDomainRespuesta(request))));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraCotizacionId}/proveedores/{compraCotizacionProveedorId}/no-cotiza")
    public ResponseEntity<CompraCotizacionProveedorResponse> marcarNoCotiza(
            @PathVariable Long compraCotizacionId,
            @PathVariable Long compraCotizacionProveedorId,
            @RequestBody(required = false) MotivoRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.marcarNoCotiza(compraCotizacionId, compraCotizacionProveedorId,
                            request == null ? null : request.motivo())));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{compraCotizacionId}/comparacion")
    public ResponseEntity<CompraCotizacionResponse> comparacion(@PathVariable Long compraCotizacionId) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.comparacion(compraCotizacionId)));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/{compraCotizacionId}/recomendar")
    public ResponseEntity<CompraCotizacionResponse> recomendar(@PathVariable Long compraCotizacionId,
                                                               @RequestBody RecomendarCompraCotizacionRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.recomendar(compraCotizacionId,
                            request.compraCotizacionProveedorId(), request.fundamento())));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraCotizacionId}/cerrar")
    public ResponseEntity<CompraCotizacionResponse> cerrar(@PathVariable Long compraCotizacionId) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(
                    compraCotizacionService.cerrar(compraCotizacionId)));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    // ---- Etapa 7: adjudicación por monto ----

    @PostMapping("/{compraCotizacionId}/adjudicar")
    public ResponseEntity<CompraAdjudicacionResponse> adjudicar(@PathVariable Long compraCotizacionId,
                                                                @RequestBody AdjudicarCompraCotizacionRequest request) {
        try {
            return ResponseEntity.ok(compraCotizacionDtoMapper.toResponse(compraCotizacionService.adjudicar(
                    compraCotizacionId, CompraAdjudicacionDecision.valueOf(request.decision()),
                    request.compraCotizacionProveedorId(), request.proveedorAdjudicadoId(), request.monto(),
                    request.fundamento(), request.autoridadUsuarioId(), request.limiteAplicado())));
        } catch (CompraCotizacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/{compraCotizacionId}/adjudicacion")
    public ResponseEntity<CompraAdjudicacionResponse> adjudicacion(@PathVariable Long compraCotizacionId) {
        return compraCotizacionService.adjudicacion(compraCotizacionId)
                .map(compraCotizacionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Adjudicación no encontrada para el proceso: " + compraCotizacionId));
    }

    public record MotivoRequest(String motivo) {
    }
}
