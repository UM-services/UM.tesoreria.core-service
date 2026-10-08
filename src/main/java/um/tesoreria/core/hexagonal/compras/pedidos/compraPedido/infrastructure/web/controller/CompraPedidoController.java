package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.service.CompraPedidoService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.AprobarCompraPedidoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.CompraPedidoCriteriaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.CompraPedidoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.CompraPedidoResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.DescartarCompraPedidoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.RechazarCompraPedidoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.mapper.CompraPedidoDtoMapper;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.mapper.CompraPedidoItemDtoMapper;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Pedido de compra. Ruta canónica única (slice nuevo).
 *
 * <p>El detalle se compone acá: la fachada del pedido maneja la cabecera y el slice de
 * ítems; la respuesta arma ambos.</p>
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraPedido")
@RequiredArgsConstructor
public class CompraPedidoController {

    private final CompraPedidoService compraPedidoService;
    private final CompraPedidoDtoMapper compraPedidoDtoMapper;
    private final CompraPedidoItemService compraPedidoItemService;
    private final CompraPedidoItemDtoMapper compraPedidoItemDtoMapper;

    @GetMapping
    public ResponseEntity<List<CompraPedidoResponse>> findAll(
            @RequestParam(required = false) CompraPedidoEstado estado,
            @RequestParam(required = false) Integer solicitanteId,
            @RequestParam(required = false) Integer dependenciaId,
            @RequestParam(required = false) List<Integer> dependenciaIds,
            @RequestParam(required = false) Integer ejercicioId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHasta) {
        CompraPedidoCriteria criteria = new CompraPedidoCriteria(estado, solicitanteId, dependenciaId,
                ejercicioId, fechaDesde, fechaHasta, dependenciaIds);
        List<CompraPedidoResponse> responses = compraPedidoService.listar(criteria).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{compraPedidoId}")
    public ResponseEntity<CompraPedidoResponse> findById(@PathVariable Integer compraPedidoId) {
        return compraPedidoService.getById(compraPedidoId)
                .map(pedido -> ResponseEntity.ok(toResponse(pedido)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido de compra no encontrado"));
    }

    @PostMapping("/search")
    public ResponseEntity<List<CompraPedidoResponse>> search(@RequestBody CompraPedidoCriteriaRequest request) {
        CompraPedidoCriteria criteria = new CompraPedidoCriteria(request.getEstado(), request.getSolicitanteId(),
                request.getDependenciaId(), request.getEjercicioId(), request.getFechaDesde(),
                request.getFechaHasta(), request.getDependenciaIds());
        List<CompraPedidoResponse> responses = compraPedidoService.listar(criteria).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/numero/{numero}")
    public ResponseEntity<CompraPedidoResponse> findByNumero(@PathVariable String numero) {
        return compraPedidoService.getByNumero(numero)
                .map(pedido -> ResponseEntity.ok(toResponse(pedido)))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido de compra no encontrado"));
    }

    @PostMapping
    public ResponseEntity<CompraPedidoResponse> create(@RequestBody CompraPedidoRequest request) {
        try {
            CompraPedido pedido = compraPedidoDtoMapper.toDomain(request);
            List<CompraPedidoItem> items = compraPedidoItemDtoMapper.toDomain(request.getItems());
            return ResponseEntity.ok(toResponse(compraPedidoService.crear(pedido, items)));
        } catch (CompraPedidoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{compraPedidoId}")
    public ResponseEntity<CompraPedidoResponse> update(@PathVariable Integer compraPedidoId,
                                                       @RequestBody CompraPedidoRequest request) {
        try {
            CompraPedido datos = compraPedidoDtoMapper.toDomain(request);
            List<CompraPedidoItem> items = compraPedidoItemDtoMapper.toDomain(request.getItems());
            return ResponseEntity.ok(toResponse(compraPedidoService.actualizar(compraPedidoId, datos, items)));
        } catch (CompraPedidoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraPedidoId}/enviar")
    public ResponseEntity<CompraPedidoResponse> enviar(@PathVariable Integer compraPedidoId,
                                                       @RequestParam(required = false) Integer usuarioId) {
        try {
            return ResponseEntity.ok(toResponse(compraPedidoService.enviar(compraPedidoId, usuarioId)));
        } catch (CompraPedidoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraPedidoId}/aprobar")
    public ResponseEntity<CompraPedidoResponse> aprobar(@PathVariable Integer compraPedidoId,
                                                        @RequestBody AprobarCompraPedidoRequest request) {
        try {
            return ResponseEntity.ok(toResponse(
                    compraPedidoService.aprobar(compraPedidoId, request.getAutorizanteId())));
        } catch (CompraPedidoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraPedidoId}/rechazar")
    public ResponseEntity<CompraPedidoResponse> rechazar(@PathVariable Integer compraPedidoId,
                                                         @RequestBody RechazarCompraPedidoRequest request) {
        try {
            return ResponseEntity.ok(toResponse(compraPedidoService.rechazar(
                    compraPedidoId, request.getAutorizanteId(), request.getMotivo())));
        } catch (CompraPedidoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PostMapping("/{compraPedidoId}/descartar")
    public ResponseEntity<CompraPedidoResponse> descartar(@PathVariable Integer compraPedidoId,
                                                          @RequestBody DescartarCompraPedidoRequest request) {
        try {
            return ResponseEntity.ok(toResponse(compraPedidoService.descartar(
                    compraPedidoId, request.getUsuarioId(), request.getMotivo())));
        } catch (CompraPedidoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (IllegalStateException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    private CompraPedidoResponse toResponse(CompraPedido pedido) {
        CompraPedidoResponse response = compraPedidoDtoMapper.toResponse(pedido);
        response.setItems(compraPedidoItemService.getByPedido(pedido.getCompraPedidoId()).stream()
                .map(compraPedidoItemDtoMapper::toResponse)
                .toList());
        return response;
    }

}
