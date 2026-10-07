package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.exception.UsuarioPermisoException;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.service.UsuarioPermisoService;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.dto.UsuarioPermisoRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.dto.UsuarioPermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.mapper.UsuarioPermisoDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/usuarioPermiso")
@RequiredArgsConstructor
public class UsuarioPermisoController {

    private final UsuarioPermisoService service;
    private final UsuarioPermisoDtoMapper dtoMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsuarioPermisoResponse>> findAllByUserId(@PathVariable Long userId) {
        List<UsuarioPermisoResponse> responses = service.findAllByUserId(userId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Crea o actualiza el override individual. Body { otorgado: 1|0 }. */
    @PutMapping("/user/{userId}/permiso/{permisoId}")
    public ResponseEntity<UsuarioPermisoResponse> set(@PathVariable Long userId,
                                                      @PathVariable Long permisoId,
                                                      @Valid @RequestBody UsuarioPermisoRequest request) {
        try {
            UsuarioPermiso saved = service.set(userId, permisoId, request.getOtorgado());
            return ResponseEntity.ok(dtoMapper.toResponse(saved));
        } catch (UsuarioPermisoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Elimina el override; el usuario vuelve a heredar de sus roles. 404 si no existía. */
    @DeleteMapping("/user/{userId}/permiso/{permisoId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long permisoId) {
        try {
            service.delete(userId, permisoId);
            return ResponseEntity.noContent().build();
        } catch (UsuarioPermisoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
