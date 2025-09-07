package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.LegajoException;
import um.tesoreria.core.kotlin.model.Legajo;
import um.tesoreria.core.service.LegajoService;

@RestController
@RequestMapping("/api/tesoreria/core/legajo")
public class LegajoController {

    private final LegajoService service;

    public LegajoController(LegajoService service) {
        this.service = service;
    }

    @GetMapping("/facultad/{facultadId}")
    public ResponseEntity<List<Legajo>> findAllByFacultadId(@PathVariable Integer facultadId) {
        return ResponseEntity.ok(service.findAllByFacultadId(facultadId));
    }

    @GetMapping("/unique/{facultadId}/{personaId}/{documentoId}")
    public ResponseEntity<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(
            @PathVariable Integer facultadId,
            @PathVariable BigDecimal personaId,
            @PathVariable Integer documentoId) {
        try {
            return ResponseEntity.ok(service.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId));
        } catch (LegajoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<Legajo> add(@RequestBody Legajo legajo) {
        return ResponseEntity.ok(service.add(legajo));
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<Legajo>> saveAll(@RequestBody List<Legajo> legajos) {
        return ResponseEntity.ok(service.saveAll(legajos));
    }

}
