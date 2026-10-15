package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.exception.UsuarioRolException;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.service.UsuarioRolService;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto.UsuarioRolRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto.UsuarioRolResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.mapper.UsuarioRolDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/usuarioRol")
@RequiredArgsConstructor
public class UsuarioRolController {

    private final UsuarioRolService service;
    private final UsuarioRolDtoMapper dtoMapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsuarioRolResponse>> findAllByUserId(@PathVariable Long userId) {
        List<UsuarioRolResponse> responses = service.findAllByUserId(userId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Asigna un rol al usuario. Idempotente: si ya lo tiene, devuelve la asignación existente. */
    @PostMapping("/")
    public ResponseEntity<UsuarioRolResponse> add(@Valid @RequestBody UsuarioRolRequest request) {
        try {
            UsuarioRol created = service.add(dtoMapper.toDomain(request));
            return ResponseEntity.ok(dtoMapper.toResponse(created));
        } catch (UsuarioRolException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Desasigna el rol del usuario. 404 si la asignación no existía. */
    @DeleteMapping("/user/{userId}/rol/{rolId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Long rolId) {
        try {
            service.delete(userId, rolId);
            return ResponseEntity.noContent().build();
        } catch (UsuarioRolException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
