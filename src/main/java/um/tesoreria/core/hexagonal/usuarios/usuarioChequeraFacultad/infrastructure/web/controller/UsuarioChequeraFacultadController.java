package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraFacultad.application.service.UsuarioChequeraFacultadService;
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

}
