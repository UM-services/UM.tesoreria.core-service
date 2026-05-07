package um.tesoreria.core.hexagonal.ubicacion.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.ubicacion.application.service.UbicacionService;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.web.dto.UbicacionResponse;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.web.mapper.UbicacionDtoMapper;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/ubicacion", "/api/tesoreria/core/ubicacion"})
@RequiredArgsConstructor
public class UbicacionController {
    private final UbicacionService service;
    private final UbicacionDtoMapper mapper;

    @GetMapping("/")
    public ResponseEntity<List<UbicacionResponse>> findAll() {
        List<UbicacionResponse> responses = service.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/sede/{geograficaId}")
    public ResponseEntity<List<UbicacionResponse>> findAllBySede(@PathVariable Integer geograficaId) {
        List<UbicacionResponse> responses = (geograficaId == 0 ? service.findAll() : service.findAllBySede(geograficaId))
                .stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
