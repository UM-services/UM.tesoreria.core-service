package um.tesoreria.core.hexagonal.usuario.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.usuario.application.exception.UsuarioException;
import um.tesoreria.core.hexagonal.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.infrastructure.web.dto.UsuarioRequest;
import um.tesoreria.core.hexagonal.usuario.infrastructure.web.dto.UsuarioResponse;
import um.tesoreria.core.hexagonal.usuario.infrastructure.web.mapper.UsuarioDtoMapper;

@RestController
@RequestMapping({"/usuario", "/api/tesoreria/core/usuario"})
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService service;
    private final UsuarioDtoMapper dtoMapper;

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
    public ResponseEntity<UsuarioResponse> update(@RequestBody UsuarioRequest request, @PathVariable Long userId) {
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
