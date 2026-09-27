package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.exception.UsuarioChequeraGeograficaException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.service.UsuarioChequeraGeograficaService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.domain.model.UsuarioChequeraGeografica;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto.UsuarioChequeraGeograficaRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.dto.UsuarioChequeraGeograficaResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.infrastructure.web.mapper.UsuarioChequeraGeograficaDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/usuarioChequeraGeografica")
@RequiredArgsConstructor
public class UsuarioChequeraGeograficaController {

    private final UsuarioChequeraGeograficaService service;
    private final UsuarioChequeraGeograficaDtoMapper mapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsuarioChequeraGeograficaResponse>> findAllByUserId(@PathVariable Long userId) {
        List<UsuarioChequeraGeograficaResponse> responses = service.findAllByUserId(userId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Asigna una sede al usuario. Idempotente: si ya la tiene, devuelve la asignación existente. */
    @PostMapping("/")
    public ResponseEntity<UsuarioChequeraGeograficaResponse> add(@Valid @RequestBody UsuarioChequeraGeograficaRequest request) {
        try {
            UsuarioChequeraGeografica created = service.add(mapper.toDomain(request));
            return ResponseEntity.ok(mapper.toResponse(created));
        } catch (UsuarioChequeraGeograficaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Desasigna la sede del usuario. 404 si la asignación no existía. */
    @DeleteMapping("/user/{userId}/geografica/{geograficaId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Integer geograficaId) {
        try {
            service.delete(userId, geograficaId);
            return ResponseEntity.noContent().build();
        } catch (UsuarioChequeraGeograficaException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
