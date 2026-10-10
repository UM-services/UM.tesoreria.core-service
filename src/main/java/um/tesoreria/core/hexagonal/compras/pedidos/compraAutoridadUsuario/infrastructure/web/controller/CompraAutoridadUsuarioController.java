package um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.controller;

import jakarta.validation.Valid;
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
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.exception.CompraAutoridadUsuarioException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.application.service.CompraAutoridadUsuarioService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.CompraAutoridadUsuarioRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.CompraAutoridadUsuarioResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.dto.LimiteAutorizacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraAutoridadUsuario.infrastructure.web.mapper.CompraAutoridadUsuarioDtoMapper;

/**
 * Asignación de perfiles de autoridad por monto a usuarios y resolución de su límite efectivo.
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraAutoridadUsuario")
@RequiredArgsConstructor
public class CompraAutoridadUsuarioController {

    private final CompraAutoridadUsuarioService compraAutoridadUsuarioService;
    private final CompraAutoridadUsuarioDtoMapper compraAutoridadUsuarioDtoMapper;

    @GetMapping("/{usuarioId}")
    public ResponseEntity<CompraAutoridadUsuarioResponse> getPerfiles(@PathVariable Integer usuarioId) {
        return ResponseEntity.ok(compraAutoridadUsuarioDtoMapper.toResponse(
                usuarioId, compraAutoridadUsuarioService.getPerfilIds(usuarioId)));
    }

    @GetMapping("/limite/{usuarioId}/{ejercicioId}")
    public ResponseEntity<LimiteAutorizacionResponse> getLimite(@PathVariable Integer usuarioId,
                                                                @PathVariable Integer ejercicioId) {
        try {
            return ResponseEntity.ok(compraAutoridadUsuarioDtoMapper.toResponse(
                    compraAutoridadUsuarioService.getLimite(usuarioId, ejercicioId)));
        } catch (CompraAutoridadUsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<CompraAutoridadUsuarioResponse> asignar(@Valid @RequestBody CompraAutoridadUsuarioRequest request) {
        try {
            compraAutoridadUsuarioService.asignar(request.getUsuarioId(), request.getAutoridadPerfilId());
            return ResponseEntity.ok(compraAutoridadUsuarioDtoMapper.toResponse(
                    request.getUsuarioId(),
                    compraAutoridadUsuarioService.getPerfilIds(request.getUsuarioId())));
        } catch (CompraAutoridadUsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/{usuarioId}/{autoridadPerfilId}")
    public ResponseEntity<CompraAutoridadUsuarioResponse> quitar(@PathVariable Integer usuarioId,
                                                                 @PathVariable Long autoridadPerfilId) {
        try {
            compraAutoridadUsuarioService.quitar(usuarioId, autoridadPerfilId);
            return ResponseEntity.ok(compraAutoridadUsuarioDtoMapper.toResponse(
                    usuarioId, compraAutoridadUsuarioService.getPerfilIds(usuarioId)));
        } catch (CompraAutoridadUsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
