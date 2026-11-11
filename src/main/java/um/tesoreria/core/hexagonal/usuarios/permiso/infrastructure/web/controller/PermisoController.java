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
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoManifestException;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.service.PermisoService;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in.SyncPermisosUseCase;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.manifest.PermisoManifestLoader;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoRequest;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.SyncPermisosRequest;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.SyncPermisosResponse;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.SyncPermisosDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/permiso")
@RequiredArgsConstructor
public class PermisoController {

    private final PermisoService service;
    private final PermisoDtoMapper dtoMapper;
    private final PermisoManifestLoader manifestLoader;
    private final SyncPermisosUseCase syncPermisosUseCase;
    private final SyncPermisosDtoMapper syncDtoMapper;

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

    /**
     * Reconcilia el catálogo contra `src/main/resources/permisos.yaml`.
     *
     * <p>{@code dryRun=true} devuelve el diff sin escribir. Es el mismo proceso
     * que corre al arrancar el servicio. Endpoint nuevo: no reemplaza el ABM
     * existente, que queda disponible sólo como consulta/escape.
     */
    @PostMapping("/sync")
    @RequierePermiso("permisos.administrar")
    public ResponseEntity<SyncPermisosResponse> sync(
            @RequestBody(required = false) SyncPermisosRequest request) {
        boolean dryRun = request != null && Boolean.TRUE.equals(request.getDryRun());
        try {
            SyncPermisosResultado resultado = syncPermisosUseCase.sync(
                    manifestLoader.load(), dryRun);
            return ResponseEntity.ok(syncDtoMapper.toResponse(resultado));
        } catch (PermisoManifestException | PermisoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
