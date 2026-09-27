package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.exception.UsuarioChequeraFacultadException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.service.UsuarioChequeraFacultadService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.domain.model.UsuarioChequeraFacultad;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.dto.UsuarioChequeraFacultadRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.dto.UsuarioChequeraFacultadResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.mapper.UsuarioChequeraFacultadDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/usuarioChequeraFacultad")
@RequiredArgsConstructor
public class UsuarioChequeraFacultadController {

    private final UsuarioChequeraFacultadService service;
    private final UsuarioChequeraFacultadDtoMapper mapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsuarioChequeraFacultadResponse>> findAllByUserId(@PathVariable Long userId) {
        List<UsuarioChequeraFacultadResponse> responses = service.findAllByUserId(userId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Asigna una facultad al usuario. Idempotente: si ya la tiene, devuelve la asignación existente. */
    @PostMapping("/")
    public ResponseEntity<UsuarioChequeraFacultadResponse> add(@Valid @RequestBody UsuarioChequeraFacultadRequest request) {
        try {
            UsuarioChequeraFacultad created = service.add(mapper.toDomain(request));
            return ResponseEntity.ok(mapper.toResponse(created));
        } catch (UsuarioChequeraFacultadException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Desasigna la facultad del usuario. 404 si la asignación no existía. */
    @DeleteMapping("/user/{userId}/facultad/{facultadId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Integer facultadId) {
        try {
            service.delete(userId, facultadId);
            return ResponseEntity.noContent().build();
        } catch (UsuarioChequeraFacultadException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
