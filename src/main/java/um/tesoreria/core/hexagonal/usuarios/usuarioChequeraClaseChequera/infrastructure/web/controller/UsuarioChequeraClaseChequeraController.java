package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.controller;

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
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.exception.UsuarioChequeraClaseChequeraException;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.service.UsuarioChequeraClaseChequeraService;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.domain.model.UsuarioChequeraClaseChequera;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto.UsuarioChequeraClaseChequeraRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.dto.UsuarioChequeraClaseChequeraResponse;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.mapper.UsuarioChequeraClaseChequeraDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/usuarioChequeraClaseChequera")
@RequiredArgsConstructor
public class UsuarioChequeraClaseChequeraController {

    private final UsuarioChequeraClaseChequeraService service;
    private final UsuarioChequeraClaseChequeraDtoMapper mapper;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsuarioChequeraClaseChequeraResponse>> findAllByUserId(@PathVariable Long userId) {
        List<UsuarioChequeraClaseChequeraResponse> responses = service.findAllByUserId(userId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /** Asigna una clase de chequera al usuario. Idempotente: si ya la tiene, devuelve la asignación existente. */
    @PostMapping("/")
    public ResponseEntity<UsuarioChequeraClaseChequeraResponse> add(@Valid @RequestBody UsuarioChequeraClaseChequeraRequest request) {
        try {
            UsuarioChequeraClaseChequera created = service.add(mapper.toDomain(request));
            return ResponseEntity.ok(mapper.toResponse(created));
        } catch (UsuarioChequeraClaseChequeraException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    /** Desasigna la clase de chequera del usuario. 404 si la asignación no existía. */
    @DeleteMapping("/user/{userId}/claseChequera/{claseChequeraId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId, @PathVariable Integer claseChequeraId) {
        try {
            service.delete(userId, claseChequeraId);
            return ResponseEntity.noContent().build();
        } catch (UsuarioChequeraClaseChequeraException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
