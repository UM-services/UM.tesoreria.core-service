package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.exception.CompraPedidoAutorizanteException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.application.service.CompraPedidoAutorizanteService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.dto.CompraPedidoAutorizanteRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.dto.CompraPedidoAutorizanteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.mapper.CompraPedidoAutorizanteDtoMapper;

/**
 * Administración de las dependencias habilitadas por usuario autorizante de envío.
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraPedidoAutorizante")
@RequiredArgsConstructor
public class CompraPedidoAutorizanteController {

    private final CompraPedidoAutorizanteService compraPedidoAutorizanteService;
    private final CompraPedidoAutorizanteDtoMapper compraPedidoAutorizanteDtoMapper;

    @GetMapping("/dependencias/{autorizanteId}")
    public ResponseEntity<CompraPedidoAutorizanteResponse> getDependencias(@PathVariable Integer autorizanteId) {
        try {
            return ResponseEntity.ok(compraPedidoAutorizanteDtoMapper.toResponse(
                    autorizanteId, compraPedidoAutorizanteService.getDependencias(autorizanteId)));
        } catch (CompraPedidoAutorizanteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public ResponseEntity<CompraPedidoAutorizanteResponse> asignar(@RequestBody CompraPedidoAutorizanteRequest request) {
        try {
            compraPedidoAutorizanteService.asignar(request.getAutorizanteId(), request.getDependenciaId());
            return ResponseEntity.ok(compraPedidoAutorizanteDtoMapper.toResponse(
                    request.getAutorizanteId(),
                    compraPedidoAutorizanteService.getDependencias(request.getAutorizanteId())));
        } catch (CompraPedidoAutorizanteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{autorizanteId}/{dependenciaId}")
    public ResponseEntity<CompraPedidoAutorizanteResponse> quitar(@PathVariable Integer autorizanteId,
                                                                  @PathVariable Integer dependenciaId) {
        try {
            compraPedidoAutorizanteService.quitar(autorizanteId, dependenciaId);
            return ResponseEntity.ok(compraPedidoAutorizanteDtoMapper.toResponse(
                    autorizanteId, compraPedidoAutorizanteService.getDependencias(autorizanteId)));
        } catch (CompraPedidoAutorizanteException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
