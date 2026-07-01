package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service.ArancelPorcentajeService;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.dto.ArancelPorcentajeRequest;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.dto.ArancelPorcentajeResponse;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.mapper.ArancelPorcentajeDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/arancelporcentaje")
@RequiredArgsConstructor
public class ArancelPorcentajeController {

    private final ArancelPorcentajeService service;
    private final ArancelPorcentajeDtoMapper dtoMapper;

    @GetMapping("/aranceltipo/{aranceltipoId}")
    public ResponseEntity<List<ArancelPorcentajeResponse>> findAllByArancelTipoId(@PathVariable Integer aranceltipoId) {
        List<ArancelPorcentajeResponse> responses = service.findAllByArancelTipoId(aranceltipoId).stream()
                .map(dtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/unique/{aranceltipoId}/{productoId}")
    public ResponseEntity<ArancelPorcentajeResponse> findByUnique(@PathVariable Integer aranceltipoId,
                                                                   @PathVariable Integer productoId) {
        try {
            ArancelPorcentaje domain = service.findByUnique(aranceltipoId, productoId);
            return ResponseEntity.ok(dtoMapper.toResponse(domain));
        } catch (ArancelPorcentajeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ArancelPorcentajeResponse> add(@RequestBody ArancelPorcentajeRequest request) {
        ArancelPorcentaje domain = dtoMapper.toDomain(request);
        ArancelPorcentaje created = service.add(domain);
        return new ResponseEntity<>(dtoMapper.toResponse(created), HttpStatus.OK);
    }

    @PutMapping("/{arancelporcentajeId}")
    public ResponseEntity<ArancelPorcentajeResponse> update(@RequestBody ArancelPorcentajeRequest request,
                                                             @PathVariable Long arancelporcentajeId) {
        ArancelPorcentaje domain = dtoMapper.toDomain(request);
        ArancelPorcentaje updated = service.update(domain, arancelporcentajeId);
        return new ResponseEntity<>(dtoMapper.toResponse(updated), HttpStatus.OK);
    }
}
