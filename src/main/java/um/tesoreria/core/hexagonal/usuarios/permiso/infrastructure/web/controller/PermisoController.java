package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.controller;

import jakarta.validation.Valid;
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
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.service.PermisoService;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoRequest;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/permiso")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService service;
    private final PermisoDtoMapper dtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<PermisoResponse>> findAll() {
        List<PermisoResponse> responses = service.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{permisoId}")
    public ResponseEntity<PermisoResponse> findById(@PathVariable Long permisoId) {
        return service.findById(permisoId)
                .map(dtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Permiso no encontrado: " + permisoId));
    }

    @PostMapping("/")
    public ResponseEntity<PermisoResponse> add(@Valid @RequestBody PermisoRequest request) {
        try {
            Permiso created = service.add(dtoMapper.toDomain(request));
            return ResponseEntity.ok(dtoMapper.toResponse(created));
        } catch (PermisoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{permisoId}")
    public ResponseEntity<PermisoResponse> update(@Valid @RequestBody PermisoRequest request,
                                                  @PathVariable Long permisoId) {
        try {
            Permiso updated = service.update(dtoMapper.toDomain(request), permisoId);
            return ResponseEntity.ok(dtoMapper.toResponse(updated));
        } catch (PermisoException e) {
            // 409: no existe o la clave ya está en uso por otro permiso.
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @DeleteMapping("/{permisoId}")
    public ResponseEntity<Void> delete(@PathVariable Long permisoId) {
        try {
            service.delete(permisoId);
            return ResponseEntity.noContent().build();
        } catch (PermisoException e) {
            // 409: no existe o tiene roles/usuarios asignados.
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
