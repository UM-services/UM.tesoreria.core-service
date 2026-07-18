package um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.service.ChequeraTotalService;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.web.dto.ChequeraTotalResponse;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.infrastructure.web.mapper.ChequeraTotalDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/chequeratotal")
@RequiredArgsConstructor
public class ChequeraTotalController {

    private final ChequeraTotalService service;
    private final ChequeraTotalDtoMapper dtoMapper;

    @GetMapping("/chequera/{facultadId}/{tipochequeraId}/{chequeraserieId}")
    public ResponseEntity<List<ChequeraTotalResponse>> findAllByChequera(
            @PathVariable Integer facultadId,
            @PathVariable Integer tipochequeraId,
            @PathVariable Long chequeraserieId) {
        List<ChequeraTotalResponse> responses = service.findAllByChequera(facultadId, tipochequeraId, chequeraserieId)
                .stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }
}
