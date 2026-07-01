package um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.exception.TipoChequeraException;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.dto.TipoChequeraRequest;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.dto.TipoChequeraResponse;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.infrastructure.web.mapper.TipoChequeraDtoMapper;
import um.tesoreria.core.model.view.TipoChequeraSearch;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/tipoChequera", "/api/tesoreria/core/tipoChequera"})
@RequiredArgsConstructor
public class TipoChequeraController {

    private final TipoChequeraService service;
    private final TipoChequeraDtoMapper tipoChequeraDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<TipoChequeraResponse>> findAll() {
        List<TipoChequeraResponse> responses = service.findAll().stream()
                .map(tipoChequeraDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/search")
    public ResponseEntity<List<TipoChequeraSearch>> findAllByStrings(@RequestBody List<String> conditions) {
        return ResponseEntity.ok(service.findAllByStrings(conditions));
    }

    @GetMapping("/asignable/{facultadId}/{lectivoId}/{geograficaId}/{claseChequeraId}")
    public ResponseEntity<List<TipoChequeraResponse>> findAllAsignable(@PathVariable Integer facultadId,
                                                                       @PathVariable Integer lectivoId,
                                                                       @PathVariable Integer geograficaId,
                                                                       @PathVariable Integer claseChequeraId) {
        List<TipoChequeraResponse> responses = service.findAllAsignable(facultadId, lectivoId, geograficaId, claseChequeraId)
                .stream().map(tipoChequeraDtoMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/facultad/{facultadId}/geografica/{geograficaId}")
    public ResponseEntity<List<TipoChequeraResponse>> findAllByFacultadIdAndGeograficaId(@PathVariable Integer facultadId,
                                                                                         @PathVariable Integer geograficaId) {
        List<TipoChequeraResponse> responses = service.findAllByFacultadIdAndGeograficaId(facultadId, geograficaId)
                .stream().map(tipoChequeraDtoMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{tipoChequeraId}")
    public ResponseEntity<TipoChequeraResponse> findByTipoChequeraId(@PathVariable Integer tipoChequeraId) {
        try {
            TipoChequera domain = service.findByTipoChequeraId(tipoChequeraId);
            return ResponseEntity.ok(tipoChequeraDtoMapper.toResponse(domain));
        } catch (TipoChequeraException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @GetMapping("/last")
    public ResponseEntity<TipoChequeraResponse> findLast() {
        try {
            TipoChequera domain = service.findLast();
            return ResponseEntity.ok(tipoChequeraDtoMapper.toResponse(domain));
        } catch (TipoChequeraException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<TipoChequeraResponse> add(@RequestBody TipoChequeraRequest request) {
        TipoChequera domain = tipoChequeraDtoMapper.toDomain(request);
        TipoChequera created = service.add(domain);
        return ResponseEntity.ok(tipoChequeraDtoMapper.toResponse(created));
    }

    @PutMapping("/{tipochequeraId}")
    public ResponseEntity<TipoChequeraResponse> update(@RequestBody TipoChequeraRequest request,
                                                       @PathVariable Integer tipochequeraId) {
        TipoChequera domain = tipoChequeraDtoMapper.toDomain(request);
        TipoChequera updated = service.update(domain, tipochequeraId);
        return ResponseEntity.ok(tipoChequeraDtoMapper.toResponse(updated));
    }

    @DeleteMapping("/{tipochequeraId}")
    public ResponseEntity<Void> delete(@PathVariable Integer tipochequeraId) {
        service.delete(tipochequeraId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/unmark")
    public ResponseEntity<Void> unmark() {
        service.unmark();
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mark/{tipochequeraId}/{imprimir}")
    public ResponseEntity<Void> mark(@PathVariable Integer tipochequeraId, @PathVariable Byte imprimir) {
        service.mark(tipochequeraId, imprimir);
        return ResponseEntity.noContent().build();
    }

}
