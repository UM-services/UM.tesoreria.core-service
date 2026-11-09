package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.controller;

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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.exception.AutorizanteEnvioException;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.application.service.AutorizanteEnvioService;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto.AutorizanteEnvioRequest;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto.AutorizanteEnvioResponse;
import um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.mapper.AutorizanteEnvioDtoMapper;

import java.util.List;

/**
 * Administración de las dependencias habilitadas de cada autorizante de envío.
 *
 * <p>El contexto es independiente del pedido: administra el conjunto usuario ↔ dependencias.
 * Las escrituras exigen {@code compras.autorizantes.configurar}; la lectura de una usuario la
 * consume también la fachada de compras, que valida el permiso de envío de su lado.</p>
 */
@RestController
@RequestMapping("/api/tesoreria/core/compras/autorizanteEnvio")
@RequiredArgsConstructor
public class AutorizanteEnvioController {

    static final String PERMISO_CONFIGURAR = "compras.autorizantes.configurar";

    private final AutorizanteEnvioService autorizanteEnvioService;
    private final AutorizanteEnvioDtoMapper autorizanteEnvioDtoMapper;

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<AutorizanteEnvioResponse> get(@PathVariable Integer usuarioId) {
        try {
            return ResponseEntity.ok(autorizanteEnvioDtoMapper.toResponse(
                    autorizanteEnvioService.get(usuarioId)));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/usuario/{usuarioId}")
    @RequierePermiso(PERMISO_CONFIGURAR)
    public ResponseEntity<AutorizanteEnvioResponse> reemplazar(@PathVariable Integer usuarioId,
                                                               @RequestBody AutorizanteEnvioRequest request) {
        try {
            return ResponseEntity.ok(autorizanteEnvioDtoMapper.toResponse(
                    autorizanteEnvioService.reemplazar(usuarioId, request.getDependenciaIds())));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/usuario/{usuarioId}/dependencia/{dependenciaId}")
    @RequierePermiso(PERMISO_CONFIGURAR)
    public ResponseEntity<AutorizanteEnvioResponse> asignar(@PathVariable Integer usuarioId,
                                                            @PathVariable Integer dependenciaId) {
        try {
            return ResponseEntity.ok(autorizanteEnvioDtoMapper.toResponse(
                    autorizanteEnvioService.asignar(usuarioId, dependenciaId)));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @DeleteMapping("/usuario/{usuarioId}/dependencia/{dependenciaId}")
    @RequierePermiso(PERMISO_CONFIGURAR)
    public ResponseEntity<AutorizanteEnvioResponse> quitar(@PathVariable Integer usuarioId,
                                                           @PathVariable Integer dependenciaId) {
        try {
            return ResponseEntity.ok(autorizanteEnvioDtoMapper.toResponse(
                    autorizanteEnvioService.quitar(usuarioId, dependenciaId)));
        } catch (AutorizanteEnvioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping
    @RequierePermiso(PERMISO_CONFIGURAR)
    public ResponseEntity<List<AutorizanteEnvioResponse>> listar() {
        return ResponseEntity.ok(autorizanteEnvioDtoMapper.toResponse(autorizanteEnvioService.listar()));
    }

}
