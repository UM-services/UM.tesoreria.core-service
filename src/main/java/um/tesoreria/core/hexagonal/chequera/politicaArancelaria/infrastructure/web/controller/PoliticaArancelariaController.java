package um.tesoreria.core.hexagonal.chequera.politicaArancelaria.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.application.service.PoliticaArancelariaService;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.infrastructure.web.dto.PoliticaArancelariaResponse;
import um.tesoreria.core.hexagonal.chequera.politicaArancelaria.infrastructure.web.mapper.PoliticaArancelariaDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/politicaArancelaria")
@RequiredArgsConstructor
public class PoliticaArancelariaController {

    private final PoliticaArancelariaService service;
    private final PoliticaArancelariaDtoMapper politicaArancelariaDtoMapper;

    @GetMapping("/recalculate/cuota/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{productoId}/{alternativaId}/{cuotaId}/plazo/{dias}")
    public ResponseEntity<PoliticaArancelariaResponse> recalculateCuotaByUnique(
            @PathVariable Integer facultadId,
            @PathVariable Integer tipoChequeraId,
            @PathVariable Long chequeraSerieId,
            @PathVariable Integer productoId,
            @PathVariable Integer alternativaId,
            @PathVariable Integer cuotaId,
            @PathVariable Integer dias) {
        var domain = service.recalculateCuotaByUniqueIndex(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId, dias);
        return ResponseEntity.ok(politicaArancelariaDtoMapper.toResponse(domain));
    }
}
