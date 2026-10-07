package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception.PermisoEfectivoException;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.service.PermisoEfectivoService;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.dto.PermisoEfectivoResponse;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.mapper.PermisoEfectivoDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/permisoEfectivo")
@RequiredArgsConstructor
public class PermisoEfectivoController {

    private final PermisoEfectivoService service;
    private final PermisoEfectivoDtoMapper dtoMapper;

    /** Bundle de claves efectivas del usuario (roles + overrides + bridge legacy). */
    @GetMapping("/usuario/{userId}")
    public ResponseEntity<PermisoEfectivoResponse> getPermisosEfectivos(@PathVariable Long userId) {
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(service.getPermisosEfectivos(userId)));
        } catch (PermisoEfectivoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
