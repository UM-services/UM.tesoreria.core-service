package um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.application.service.LectivoTotalImputacionService;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionRequest;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.dto.LectivoTotalImputacionResponse;
import um.tesoreria.core.hexagonal.contable.lectivoTotalImputacion.infrastructure.web.mapper.LectivoTotalImputacionDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/lectivototalimputacion")
@RequiredArgsConstructor
public class LectivoTotalImputacionController {

    private final LectivoTotalImputacionService service;
    private final LectivoTotalImputacionDtoMapper mapper;

    @GetMapping("/tipo/{facultadId}/{lectivoId}/{tipochequeraId}")
    public ResponseEntity<List<LectivoTotalImputacionResponse>> findAllByTipo(
            @PathVariable Integer facultadId,
            @PathVariable Integer lectivoId,
            @PathVariable Integer tipochequeraId) {
        List<LectivoTotalImputacionResponse> responses = service.findAllByTipo(facultadId, lectivoId, tipochequeraId)
                .stream().map(mapper::toResponse).collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/producto/{facultadId}/{lectivoId}/{tipochequeraId}/{productoId}")
    public ResponseEntity<LectivoTotalImputacionResponse> findByProducto(
            @PathVariable Integer facultadId,
            @PathVariable Integer lectivoId,
            @PathVariable Integer tipochequeraId,
            @PathVariable Integer productoId) {
        return service.findByProducto(facultadId, lectivoId, tipochequeraId, productoId)
                .map(domain -> new ResponseEntity<>(mapper.toResponse(domain), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/")
    public ResponseEntity<LectivoTotalImputacionResponse> add(@RequestBody LectivoTotalImputacionRequest request) {
        LectivoTotalImputacionResponse response = mapper.toResponse(service.add(mapper.toDomain(request)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/{lectivoTotalImputacionId}")
    public ResponseEntity<LectivoTotalImputacionResponse> update(
            @RequestBody LectivoTotalImputacionRequest request,
            @PathVariable Long lectivoTotalImputacionId) {
        return service.update(lectivoTotalImputacionId, mapper.toDomain(request))
                .map(domain -> new ResponseEntity<>(mapper.toResponse(domain), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
