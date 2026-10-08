package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.service.CompraPedidoHistorialService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.dto.CompraPedidoHistorialResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.mapper.CompraPedidoHistorialDtoMapper;

import java.util.List;

/**
 * Línea de tiempo de estados de un pedido de compra.
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraPedidoHistorial")
@RequiredArgsConstructor
public class CompraPedidoHistorialController {

    private final CompraPedidoHistorialService compraPedidoHistorialService;
    private final CompraPedidoHistorialDtoMapper compraPedidoHistorialDtoMapper;

    @GetMapping("/{compraPedidoId}")
    public ResponseEntity<List<CompraPedidoHistorialResponse>> findByPedido(@PathVariable Integer compraPedidoId) {
        List<CompraPedidoHistorialResponse> responses = compraPedidoHistorialService.listar(compraPedidoId).stream()
                .map(compraPedidoHistorialDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

}
