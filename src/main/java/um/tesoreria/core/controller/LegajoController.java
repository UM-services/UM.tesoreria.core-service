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

    @GetMapping("/unique/{facultadId}/{personaId}/{documentoId}")
    public ResponseEntity<Legajo> findByFacultadIdAndPersonaIdAndDocumentoId(
            @PathVariable Integer facultadId,
            @PathVariable BigDecimal personaId,
            @PathVariable Integer documentoId) {
        try {
            return new ResponseEntity<>(service.findByFacultadIdAndPersonaIdAndDocumentoId(facultadId, personaId, documentoId), HttpStatus.OK);
        } catch (LegajoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<Legajo> add(@RequestBody Legajo legajo) {
        return new ResponseEntity<>(service.add(legajo), HttpStatus.OK);
    }

    @PostMapping("/saveAll")
    public ResponseEntity<List<Legajo>> saveAll(@RequestBody List<Legajo> legajos) {
        return new ResponseEntity<>(service.saveAll(legajos), HttpStatus.OK);
    }

}
