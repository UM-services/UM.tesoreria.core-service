package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.exception.AutorizanteEnvioException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.service.AutorizanteEnvioService;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto.CompraPedidoAutorizanteLegacyRequest;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto.CompraPedidoAutorizanteLegacyResponse;

import java.util.List;

/**
 * Alias temporal de la API anterior de autorizantes de envío.
 *
 * @deprecated usar {@code /api/tesoreria/core/compras/autorizanteEnvio}. Se conserva durante
 * una release para permitir el despliegue escalonado de compras-service y del frontend.
 */
@Deprecated
@RestController
@RequestMapping("/api/tesoreria/core/compraPedidoAutorizante")
@RequiredArgsConstructor
public class CompraPedidoAutorizanteLegacyController {

    private final AutorizanteEnvioService autorizanteEnvioService;

    @GetMapping("/dependencias/{autorizanteId}")
    public ResponseEntity<CompraPedidoAutorizanteLegacyResponse> getDependencias(
            @PathVariable Integer autorizanteId) {
        try {
            return ResponseEntity.ok(toResponse(autorizanteId,
                    autorizanteEnvioService.get(autorizanteId).dependenciaIdsOrdenadas()));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public ResponseEntity<CompraPedidoAutorizanteLegacyResponse> asignar(
            @RequestBody CompraPedidoAutorizanteLegacyRequest request) {
        try {
            var autorizante = autorizanteEnvioService.asignar(
                    request.getAutorizanteId(), request.getDependenciaId());
            return ResponseEntity.ok(toResponse(request.getAutorizanteId(),
                    autorizante.dependenciaIdsOrdenadas()));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{autorizanteId}/{dependenciaId}")
    public ResponseEntity<CompraPedidoAutorizanteLegacyResponse> quitar(
            @PathVariable Integer autorizanteId,
            @PathVariable Integer dependenciaId) {
        try {
            var autorizante = autorizanteEnvioService.quitar(autorizanteId, dependenciaId);
            return ResponseEntity.ok(toResponse(autorizanteId, autorizante.dependenciaIdsOrdenadas()));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private CompraPedidoAutorizanteLegacyResponse toResponse(Integer autorizanteId,
                                                             List<Integer> dependenciaIds) {
        return CompraPedidoAutorizanteLegacyResponse.builder()
                .autorizanteId(autorizanteId)
                .dependenciaIds(dependenciaIds)
                .build();
    }

}
