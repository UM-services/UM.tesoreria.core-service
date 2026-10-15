package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.exception.RolPermisoException;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.service.RolPermisoService;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto.RolPermisoRequest;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto.RolPermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.mapper.RolPermisoDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/rolPermiso")
@RequiredArgsConstructor
public class RolPermisoController {

    private final RolPermisoService service;
    private final RolPermisoDtoMapper dtoMapper;

    @GetMapping("/rol/{rolId}")
    public ResponseEntity<List<RolPermisoResponse>> findAllByRolId(@PathVariable Long rolId) {
        List<RolPermisoResponse> responses = service.findAllByRolId(rolId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Asigna un permiso al rol. Idempotente: si ya lo tiene, devuelve la asignación existente. */
    @PostMapping("/")
    public ResponseEntity<RolPermisoResponse> add(@Valid @RequestBody RolPermisoRequest request) {
        try {
            RolPermiso created = service.add(dtoMapper.toDomain(request));
            return ResponseEntity.ok(dtoMapper.toResponse(created));
        } catch (RolPermisoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Desasigna el permiso del rol. 404 si la asignación no existía. */
    @DeleteMapping("/rol/{rolId}/permiso/{permisoId}")
    public ResponseEntity<Void> delete(@PathVariable Long rolId, @PathVariable Long permisoId) {
        try {
            service.delete(rolId, permisoId);
            return ResponseEntity.noContent().build();
        } catch (RolPermisoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
