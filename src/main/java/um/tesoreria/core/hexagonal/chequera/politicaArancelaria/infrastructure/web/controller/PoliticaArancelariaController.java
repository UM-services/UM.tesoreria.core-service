package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.service.PoliticaArancelariaService;

@RestController
@RequestMapping("/api/tesoreria/core/politicaArancelaria")
@RequiredArgsConstructor
public class PoliticaArancelariaController {

    private final PoliticaArancelariaService service;

    @GetMapping("/recalculate/cuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}")
    public ResponseEntity<?> recalculateCuotaByUnique(
            @PathVariable Integer facultadId,
            @PathVariable Integer tipoChequeraId,
            @PathVariable Long chequeraSerieId,
            @PathVariable Integer productoId,
            @PathVariable Integer alternativaId,
            @PathVariable Integer cuotaId) {
        return ResponseEntity.ok(service.recalculateCuotaByUniqueIndex(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId));
    }
}
