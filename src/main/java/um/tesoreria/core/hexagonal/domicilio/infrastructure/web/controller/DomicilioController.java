package um.tesoreria.core.hexagonal.domicilio.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.domicilio.application.service.DomicilioService;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.web.dto.DomicilioRequest;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.web.dto.DomicilioResponse;
import um.tesoreria.core.hexagonal.domicilio.infrastructure.web.mapper.DomicilioDtoMapper;
import um.tesoreria.core.model.view.DomicilioKey;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping({"/domicilio", "/api/tesoreria/core/domicilio"})
@RequiredArgsConstructor
public class DomicilioController {

    private final DomicilioService domicilioService;
    private final DomicilioDtoMapper domicilioDtoMapper;

    @PostMapping("/")
    public ResponseEntity<DomicilioResponse> create(@RequestBody DomicilioRequest request) {
        var domicilio = domicilioDtoMapper.toDomain(request);
        var created = domicilioService.create(domicilio);
        return new ResponseEntity<>(domicilioDtoMapper.toResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("/{domicilioId}")
    public ResponseEntity<DomicilioResponse> findById(@PathVariable Long domicilioId) {
        return domicilioService.findById(domicilioId)
                .map(domain -> new ResponseEntity<>(domicilioDtoMapper.toResponse(domain), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/unique/{personaId}/{documentoId}")
    public ResponseEntity<DomicilioResponse> findByUnique(@PathVariable BigDecimal personaId,
                                                           @PathVariable Integer documentoId) {
        try {
            return ResponseEntity.ok(domicilioDtoMapper.toResponse(domicilioService.findByUnique(personaId, documentoId)));
        } catch (DomicilioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/pagador/{facultadId}/{personaId}/{documentoId}/{lectivoId}")
    public ResponseEntity<DomicilioResponse> findWithPagador(@PathVariable Integer facultadId,
                                                              @PathVariable BigDecimal personaId,
                                                              @PathVariable Integer documentoId,
                                                              @PathVariable Integer lectivoId) {
        var domain = domicilioService.findWithPagador(facultadId, personaId, documentoId, lectivoId);
        return new ResponseEntity<>(domicilioDtoMapper.toResponse(domain), HttpStatus.OK);
    }

    @PostMapping("/unifieds")
    public ResponseEntity<List<DomicilioKey>> findAllByUnifieds(@RequestBody List<String> unifieds) {
        return new ResponseEntity<>(domicilioService.findAllByUnifieds(unifieds), HttpStatus.OK);
    }

    @PutMapping("/{domicilioId}")
    public ResponseEntity<DomicilioResponse> update(@PathVariable Long domicilioId,
                                                     @RequestBody DomicilioRequest request) {
        var domicilio = domicilioDtoMapper.toDomain(request);
        return domicilioService.update(domicilioId, domicilio)
                .map(domain -> new ResponseEntity<>(domicilioDtoMapper.toResponse(domain), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/sincronize")
    public ResponseEntity<DomicilioResponse> sincronize(@RequestBody DomicilioRequest request) {
        var domicilio = domicilioDtoMapper.toDomain(request);
        var result = domicilioService.sincronize(domicilio);
        return new ResponseEntity<>(domicilioDtoMapper.toResponse(result), HttpStatus.OK);
    }

    @GetMapping("/capture/{personaId}/{documentoId}")
    public ResponseEntity<Integer> capture(@PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
        return new ResponseEntity<>(domicilioService.capture(personaId, documentoId), HttpStatus.OK);
    }
}
