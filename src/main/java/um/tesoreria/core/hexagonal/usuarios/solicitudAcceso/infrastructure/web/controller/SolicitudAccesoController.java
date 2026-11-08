package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.exception.SolicitudAccesoException;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.service.SolicitudAccesoService;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.domain.model.SolicitudAcceso;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto.ResolverSolicitudAccesoRequest;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto.SolicitudAccesoRequest;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.dto.SolicitudAccesoResponse;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.web.mapper.SolicitudAccesoDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Bandeja de solicitudes de acceso.
 *
 * <p>Endpoints NUEVOS: no tocan `/auth/*` ni ningún contrato legacy. El alta
 * acepta el `userId` del cuerpo (la identidad todavía es transitoria hasta el
 * JWT de M3, igual que el resto de los endpoints del core); la lectura de
 * pendientes y la resolución exigen {@code permisos.administrar} cuando el PEP
 * del core está activo ({@code app.permissions.enforce=true}). Con el
 * enforcement apagado (default) sólo la pantalla de administración restringe el
 * acceso, igual que el resto de las pantallas de permisos.
 *
 * <p>La aprobación registra la decisión pero NO otorga la clave: el
 * administrador debe asignarla desde la pantalla de Usuarios para mantener un
 * único camino de escritura sobre el motor de permisos.
 */
@RestController
@RequestMapping("/api/tesoreria/core/solicitudAcceso")
@RequiredArgsConstructor
public class SolicitudAccesoController {

    private final SolicitudAccesoService service;
    private final SolicitudAccesoDtoMapper dtoMapper;

    @PostMapping("/")
    public ResponseEntity<SolicitudAccesoResponse> create(
            @Valid @RequestBody SolicitudAccesoRequest request) {
        try {
            SolicitudAcceso created = service.create(dtoMapper.toDomain(request));
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toResponse(created));
        } catch (SolicitudAccesoException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @GetMapping("/pendiente")
    @RequierePermiso("permisos.administrar")
    public ResponseEntity<List<SolicitudAccesoResponse>> pendientes() {
        return ResponseEntity.ok(toResponses(service.findPendientes()));
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<List<SolicitudAccesoResponse>> porUsuario(@PathVariable Long userId) {
        return ResponseEntity.ok(toResponses(service.findByUserId(userId)));
    }

    @PutMapping("/{solicitudAccesoId}/resolver")
    @RequierePermiso("permisos.administrar")
    public ResponseEntity<SolicitudAccesoResponse> resolver(
            @PathVariable Long solicitudAccesoId,
            @Valid @RequestBody ResolverSolicitudAccesoRequest request) {
        try {
            SolicitudAcceso resuelta = service.resolve(
                    solicitudAccesoId,
                    request.getEstado(),
                    request.getObservacion(),
                    request.getResueltoPor());
            return ResponseEntity.ok(dtoMapper.toResponse(resuelta));
        } catch (SolicitudAccesoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    private List<SolicitudAccesoResponse> toResponses(List<SolicitudAcceso> solicitudes) {
        return solicitudes.stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
    }
}
