package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service.CompraPedidoItemService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.dto.CompraPedidoItemResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.mapper.CompraPedidoItemDtoMapper;

import java.util.List;

/**
 * Consulta del detalle de un pedido. El alta y la edición del detalle se hacen siempre a
 * través del pedido ({@code /api/tesoreria/core/compraPedido}).
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraPedidoItem")
@RequiredArgsConstructor
public class CompraPedidoItemController {

    private final CompraPedidoItemService compraPedidoItemService;
    private final CompraPedidoItemDtoMapper compraPedidoItemDtoMapper;

    @GetMapping("/pedido/{compraPedidoId}")
    public ResponseEntity<List<CompraPedidoItemResponse>> findByPedido(@PathVariable Integer compraPedidoId) {
        List<CompraPedidoItemResponse> responses = compraPedidoItemService.getByPedido(compraPedidoId).stream()
                .map(compraPedidoItemDtoMapper::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

}
