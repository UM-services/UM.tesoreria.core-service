package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.usuarios.usuarioChequeraClaseChequera.application.service.UsuarioChequeraClaseChequeraService;
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

}
