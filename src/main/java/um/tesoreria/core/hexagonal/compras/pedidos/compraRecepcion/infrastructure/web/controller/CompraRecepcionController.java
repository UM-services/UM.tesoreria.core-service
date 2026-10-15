package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.exception.CompraRecepcionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.application.service.CompraRecepcionService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CerrarCompraRecepcionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionCriteriaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.mapper.CompraRecepcionDtoMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Recepción y conformidad (etapa 9): entregas contra una Orden de Compra. Ruta canónica
 * {@code /api/tesoreria/core/compraRecepcion}.
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraRecepcion")
@RequiredArgsConstructor
public class CompraRecepcionController {

    private final CompraRecepcionService compraRecepcionService;
    private final CompraRecepcionDtoMapper compraRecepcionDtoMapper;

    @GetMapping
    public ResponseEntity<List<CompraRecepcionResponse>> findAll(
            @RequestParam(required = false) Integer ejercicioId,
            @RequestParam(required = false) Long compraFormalizacionId,
            @RequestParam(required = false) String resultado,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        CompraRecepcionCriteria criteria = new CompraRecepcionCriteria(ejercicioId, compraFormalizacionId,
                parseResultado(resultado), fechaDesde, fechaHasta);
        return ResponseEntity.ok(compraRecepcionService.listar(criteria).stream()
                .map(compraRecepcionDtoMapper::toResponse)
                .toList());
    }

    @PostMapping("/search")
    public ResponseEntity<List<CompraRecepcionResponse>> search(@RequestBody CompraRecepcionCriteriaRequest request) {
        CompraRecepcionCriteria criteria = new CompraRecepcionCriteria(request.getEjercicioId(),
                request.getCompraFormalizacionId(), parseResultado(request.getResultado()),
                request.getFechaDesde(), request.getFechaHasta());
        return ResponseEntity.ok(compraRecepcionService.listar(criteria).stream()
                .map(compraRecepcionDtoMapper::toResponse)
                .toList());
    }

    /** Órdenes de Compra del ejercicio con recepción pendiente. */
    @GetMapping("/pendientes")
    public ResponseEntity<List<CompraRecepcionPendienteResponse>> pendientes(
            @RequestParam(required = false) Integer ejercicioId) {
        return ResponseEntity.ok(compraRecepcionService.pendientes(ejercicioId).stream()
                .map(compraRecepcionDtoMapper::toResponse)
                .toList());
    }

    /** Recepciones y saldos de una Orden de Compra. */
    @GetMapping("/por-formalizacion/{compraFormalizacionId}")
    public ResponseEntity<CompraRecepcionPendienteResponse> porFormalizacion(
            @PathVariable Long compraFormalizacionId) {
        return compraRecepcionService.porFormalizacion(compraFormalizacionId)
                .map(compraRecepcionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Orden de Compra no encontrada: " + compraFormalizacionId));
    }

    @GetMapping("/{compraRecepcionId}")
    public ResponseEntity<CompraRecepcionResponse> findById(@PathVariable Long compraRecepcionId) {
        return compraRecepcionService.getById(compraRecepcionId)
                .map(compraRecepcionDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Recepción no encontrada: " + compraRecepcionId));
    }

    /** Registra una entrega (total o parcial) contra una Orden de Compra. */
    @PostMapping
    public ResponseEntity<CompraRecepcionResponse> registrar(@RequestBody CompraRecepcionRequest request) {
        if (request == null || request.getCompraFormalizacionId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "compraFormalizacionId es requerido");
        }
        try {
            return ResponseEntity.ok(compraRecepcionDtoMapper.toResponse(
                    compraRecepcionService.registrar(request.getCompraFormalizacionId(),
                            compraRecepcionDtoMapper.toDomain(request), request.getUsuarioId())));
        } catch (CompraRecepcionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Cierre manual de la recepción de una OC con saldo abierto (motivo obligatorio). */
    @PostMapping("/formalizacion/{compraFormalizacionId}/cerrar")
    public ResponseEntity<Void> cerrar(@PathVariable Long compraFormalizacionId,
                                       @RequestBody CerrarCompraRecepcionRequest request) {
        try {
            compraRecepcionService.cerrar(compraFormalizacionId,
                    request == null ? null : request.getMotivo(),
                    request == null ? null : request.getUsuarioId());
            return ResponseEntity.noContent().build();
        } catch (CompraRecepcionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalArgumentException | IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private static CompraRecepcionResultado parseResultado(String valor) {
        return valor == null || valor.isBlank() ? null
                : CompraRecepcionResultado.valueOf(valor.trim().toUpperCase());
    }

}
