package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.UsuarioException;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioConfiguracionRequest;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioPasswordRequest;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioRequest;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.dto.UsuarioResponse;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.web.mapper.UsuarioDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/usuario", "/api/tesoreria/core/usuario"})
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioDtoMapper dtoMapper;

    /** Búsqueda de usuarios activos por login o nombre (texto vacío = padrón completo). */
    @GetMapping("/search")
    public ResponseEntity<List<UsuarioResponse>> search(@RequestParam(name = "q", required = false) String q) {
        List<UsuarioResponse> responses = service.search(q).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Padrón completo de usuarios (activos e inactivos). */
    @GetMapping("/searchTodos")
    public ResponseEntity<List<UsuarioResponse>> searchTodos() {
        return searchTodos(null);
    }

    /** Búsqueda por login o nombre incluyendo inactivos. */
    @GetMapping("/searchTodos/{texto}")
    public ResponseEntity<List<UsuarioResponse>> searchTodos(@PathVariable String texto) {
        List<UsuarioResponse> responses = service.searchTodos(texto).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/usuario/id/{userId}")
    public ResponseEntity<UsuarioResponse> findByUserId(@PathVariable Long userId) {
        Usuario usuario = service.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return ResponseEntity.ok(dtoMapper.toResponse(usuario));
    }

    /** Edita la configuración (datos y flags) sin tocar login ni clave. */
    @PutMapping("/usuario/{userId}/configuracion")
    public ResponseEntity<UsuarioResponse> updateConfiguracion(@Valid @RequestBody UsuarioConfiguracionRequest request,
                                                               @PathVariable Long userId) {
        try {
            Usuario cambios = dtoMapper.toDomainConfiguracion(request);
            return ResponseEntity.ok(dtoMapper.toResponse(service.updateConfiguracion(cambios, userId)));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /** Habilita (1) o deshabilita (0) un usuario. */
    @PutMapping("/usuario/{userId}/activo/{valor}")
    public ResponseEntity<UsuarioResponse> updateEstado(@PathVariable Long userId, @PathVariable Integer valor) {
        if (valor != 0 && valor != 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El valor de activo debe ser 0 o 1");
        }
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(service.updateEstado(userId, valor.byteValue())));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    /** Resetea la clave del usuario sin exigir la anterior (administración). */
    @PutMapping("/usuario/{userId}/password")
    public ResponseEntity<UsuarioResponse> resetPassword(@Valid @RequestBody UsuarioPasswordRequest request,
                                                         @PathVariable Long userId) {
        if (request.getReClave() != null && !request.getReClave().trim().equals(request.getPassword().trim())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Las claves no coinciden");
        }
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(service.resetPassword(userId, request.getPassword().trim())));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/usuario/{login}")
    public ResponseEntity<UsuarioResponse> findByLogin(@PathVariable String login) {
        try {
            Usuario domain = service.findByLogin(login);
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/usuario")
    public ResponseEntity<UsuarioResponse> add(@RequestBody UsuarioRequest request) {
        Usuario domain = dtoMapper.toDomain(request);
        Usuario created = service.add(domain);
        return ResponseEntity.ok(dtoMapper.toResponse(created));
    }

    @PutMapping("/usuario/{userId}")
    public ResponseEntity<UsuarioResponse> update(@Valid @RequestBody UsuarioRequest request, @PathVariable Long userId) {
        Usuario domain = dtoMapper.toDomain(request);
        Usuario updated = service.update(domain, userId);
        return ResponseEntity.ok(dtoMapper.toResponse(updated));
    }

    @PutMapping("/password")
    public ResponseEntity<UsuarioResponse> findByPassword(@RequestBody UsuarioRequest request) {
        try {
            Usuario domain = service.findByPassword(request.getPassword());
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/lastLog/{userId}")
    public ResponseEntity<UsuarioResponse> updateLastLog(@PathVariable Long userId) {
        try {
            Usuario domain = service.updateLastLog(userId);
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/google/mail/{googleMail}")
    public ResponseEntity<UsuarioResponse> findByGoogleMail(@PathVariable String googleMail) {
        try {
            Usuario domain = service.findByGoogleMail(googleMail);
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (UsuarioException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
