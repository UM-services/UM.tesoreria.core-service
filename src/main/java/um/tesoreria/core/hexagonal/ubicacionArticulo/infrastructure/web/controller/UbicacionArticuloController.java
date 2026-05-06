package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.service.UbicacionArticuloService;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloResponse;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.mapper.UbicacionArticuloDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/ubicacionArticulo", "/api/tesoreria/core/ubicacionArticulo"})
@RequiredArgsConstructor
public class UbicacionArticuloController {
    private final UbicacionArticuloService service;
    private final UbicacionArticuloDtoMapper mapper;

    @GetMapping("/")
    public ResponseEntity<List<UbicacionArticuloResponse>> findAll() {
        List<UbicacionArticuloResponse> responses = service.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{ubicacionId}/{articuloId}")
    public ResponseEntity<UbicacionArticuloResponse> findByUbicacionAndArticulo(
            @PathVariable Integer ubicacionId, @PathVariable Long articuloId) {
        return service.getByUbicacionAndArticulo(ubicacionId, articuloId)
                .map(ua -> new ResponseEntity<>(mapper.toResponse(ua), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/articulo/{articuloId}")
    public ResponseEntity<List<UbicacionArticuloResponse>> findAllByArticuloId(@PathVariable Long articuloId) {
        List<UbicacionArticuloResponse> responses = service.findAllByArticuloId(articuloId).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<UbicacionArticuloResponse> save(@RequestBody UbicacionArticuloRequest request) {
        UbicacionArticulo domain = mapper.toDomain(request);
        UbicacionArticulo saved = service.save(domain);
        return new ResponseEntity<>(mapper.toResponse(saved), HttpStatus.OK);
    }
}
