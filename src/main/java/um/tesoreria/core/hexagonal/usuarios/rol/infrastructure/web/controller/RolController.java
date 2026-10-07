package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.application.service.RolService;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolRequest;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolResponse;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper.RolDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/rol")
@RequiredArgsConstructor
public class RolController {

    private final RolService service;
    private final RolDtoMapper dtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<RolResponse>> findAll() {
        List<RolResponse> responses = service.findAll().stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{rolId}")
    public ResponseEntity<RolResponse> findById(@PathVariable Long rolId) {
        return service.findById(rolId)
                .map(dtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Rol no encontrado: " + rolId));
    }

    @PostMapping("/")
    public ResponseEntity<RolResponse> add(@Valid @RequestBody RolRequest request) {
        try {
            Rol created = service.add(dtoMapper.toDomain(request));
            return ResponseEntity.ok(dtoMapper.toResponse(created));
        } catch (RolException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PutMapping("/{rolId}")
    public ResponseEntity<RolResponse> update(@Valid @RequestBody RolRequest request, @PathVariable Long rolId) {
        try {
            Rol updated = service.update(dtoMapper.toDomain(request), rolId);
            return ResponseEntity.ok(dtoMapper.toResponse(updated));
        } catch (RolException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{rolId}")
    public ResponseEntity<Void> delete(@PathVariable Long rolId) {
        try {
            service.delete(rolId);
            return ResponseEntity.noContent().build();
        } catch (RolException e) {
            // 409: el rol existe pero tiene usuarios o permisos asignados (o no existe).
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }
}
