package um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.chequera.claseChequera.application.service.ClaseChequeraService;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.dto.ClaseChequeraResponse;
import um.tesoreria.core.hexagonal.chequera.claseChequera.infrastructure.web.mapper.ClaseChequeraDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/clasechequera")
@RequiredArgsConstructor
public class ClaseChequeraController {

    private final ClaseChequeraService claseChequeraService;
    private final ClaseChequeraDtoMapper claseChequeraDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<ClaseChequeraResponse>> findAll() {
        List<ClaseChequeraResponse> responses = claseChequeraService.findAll().stream()
                .map(claseChequeraDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/posgrado")
    public ResponseEntity<List<ClaseChequeraResponse>> findAllByPosgrado() {
        List<ClaseChequeraResponse> responses = claseChequeraService.findAllByPosgrado().stream()
                .map(claseChequeraDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/curso")
    public ResponseEntity<List<ClaseChequeraResponse>> findAllByCurso() {
        List<ClaseChequeraResponse> responses = claseChequeraService.findAllByCurso().stream()
                .map(claseChequeraDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/titulo")
    public ResponseEntity<List<ClaseChequeraResponse>> findAllByTitulo() {
        List<ClaseChequeraResponse> responses = claseChequeraService.findAllByTitulo().stream()
                .map(claseChequeraDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

}
