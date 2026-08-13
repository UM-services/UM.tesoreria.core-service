package um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.personas.legajo.application.exception.LegajoException;
import um.tesoreria.core.hexagonal.personas.legajo.application.service.LegajoService;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.dto.LegajoRequest;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.dto.LegajoResponse;
import um.tesoreria.core.hexagonal.personas.legajo.infrastructure.web.mapper.LegajoDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/legajo")
public class LegajoController {

    private final LegajoService service;
    private final LegajoDtoMapper dtoMapper;

    public LegajoController(LegajoService service, LegajoDtoMapper dtoMapper) {
        this.service = service;
        this.dtoMapper = dtoMapper;
    }

    @GetMapping("/facultad/{facultadId}")
    public ResponseEntity<List<LegajoResponse>> findAllByFacultadId(@PathVariable Integer facultadId) {
        return ResponseEntity.ok(dtoMapper.toResponse(service.findAllByFacultadId(facultadId)));
    }

    @GetMapping("/unique/{facultadId}/{personaId}/{documentoId}")
    public ResponseEntity<LegajoResponse> findByFacultadIdAndPersonaIdAndDocumentoId(
            @PathVariable Integer facultadId,
            @PathVariable BigDecimal personaId,
            @PathVariable Integer documentoId) {
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(
                    service.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId)));
        } catch (LegajoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<LegajoResponse> add(@RequestBody LegajoRequest legajo) {
        return ResponseEntity.ok(dtoMapper.toResponse(service.add(dtoMapper.toDomain(legajo))));
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<LegajoResponse>> saveAll(@RequestBody List<LegajoRequest> legajos) {
        return ResponseEntity.ok(dtoMapper.toResponse(service.saveAll(dtoMapper.toDomain(legajos))));
    }

}