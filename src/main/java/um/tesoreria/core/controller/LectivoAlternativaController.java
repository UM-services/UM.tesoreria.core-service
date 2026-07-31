package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.LectivoAlternativaException;
import um.tesoreria.core.kotlin.model.LectivoAlternativa;
import um.tesoreria.core.service.LectivoAlternativaService;

@RestController
@RequestMapping("/api/tesoreria/core/lectivoAlternativa")
public class LectivoAlternativaController {

    private final LectivoAlternativaService service;

    public LectivoAlternativaController(LectivoAlternativaService service) {
        this.service = service;
    }

    @GetMapping("/tipo/{facultadId}/{lectivoId}/{tipoChequeraId}/{alternativaId}")
    public ResponseEntity<List<LectivoAlternativa>> findAllByTipo(
            @PathVariable Integer facultadId,
            @PathVariable Integer lectivoId,
            @PathVariable Integer tipoChequeraId,
            @PathVariable Integer alternativaId) {
        return ResponseEntity.ok(service.findAllByTipo(facultadId, lectivoId, tipoChequeraId, alternativaId));
    }

    @GetMapping("/unique/{facultadId}/{lectivoId}/{tipoChequeraId}/{productoId}/{alternativaId}")
    public ResponseEntity<LectivoAlternativa> findByFacultadIdAndLectivoIdAndTipochequeraIdAndProductoIdAndAlternativaId(
            @PathVariable Integer facultadId,
            @PathVariable Integer lectivoId,
            @PathVariable Integer tipoChequeraId,
            @PathVariable Integer productoId,
            @PathVariable Integer alternativaId) {
        try {
            return ResponseEntity.ok(service.findByFacultadIdAndLectivoIdAndTipochequeraIdAndProductoIdAndAlternativaId( facultadId, lectivoId, tipoChequeraId, productoId, alternativaId));
        } catch (LectivoAlternativaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        }
    }
}
