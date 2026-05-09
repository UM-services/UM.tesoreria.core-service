package um.tesoreria.core.hexagonal.facultad.infrastructure.web.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.FacultadException;
import um.tesoreria.core.hexagonal.facultad.infrastructure.web.dto.FacultadResponse;
import um.tesoreria.core.hexagonal.facultad.infrastructure.web.mapper.FacultadDtoMapper;
import um.tesoreria.core.model.view.FacultadLectivo;
import um.tesoreria.core.model.view.FacultadLectivoSede;
import um.tesoreria.core.model.view.FacultadPersona;
import um.tesoreria.core.hexagonal.facultad.application.service.FacultadService;

@RestController
@RequestMapping({"/facultad", "/api/tesoreria/core/facultad"})
@RequiredArgsConstructor
public class FacultadController {

    private final FacultadService service;
    private final FacultadDtoMapper mapper;

    @GetMapping("/")
    public ResponseEntity<List<FacultadResponse>> findAll() {
        List<FacultadResponse> responses = service.findAll().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/facultades")
    public ResponseEntity<List<FacultadResponse>> findFacultades() {
        List<FacultadResponse> responses = service.findFacultades().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{facultadId}")
    public ResponseEntity<FacultadResponse> findByFacultadId(@PathVariable Integer facultadId) {
        try {
            return ResponseEntity.ok(mapper.toResponse(service.findByFacultadId(facultadId)));
        } catch (FacultadException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    // Legacy View Endpoints
    @GetMapping("/lectivo/{lectivoId}")
    public ResponseEntity<List<FacultadLectivo>> findAllByLectivoId(@PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllByLectivoId(lectivoId));
    }

    @GetMapping("/bypersona/{personaId}/{documentoId}/{lectivoId}")
    public ResponseEntity<List<FacultadPersona>> findAllByPersona(
            @PathVariable BigDecimal personaId, @PathVariable Integer documentoId, @PathVariable Integer lectivoId) {
        return ResponseEntity.ok(service.findAllByPersona(personaId, documentoId, lectivoId));
    }

    @GetMapping("/disenho/{lectivoId}/{geograficaId}")
    public ResponseEntity<List<FacultadLectivoSede>> findAllByDisenho(
            @PathVariable Integer lectivoId, @PathVariable Integer geograficaId) {
        return ResponseEntity.ok(service.findAllByDisenho(lectivoId, geograficaId));
    }
}
