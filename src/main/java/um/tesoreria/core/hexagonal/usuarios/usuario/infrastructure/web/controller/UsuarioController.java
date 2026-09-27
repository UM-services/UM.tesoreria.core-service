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
