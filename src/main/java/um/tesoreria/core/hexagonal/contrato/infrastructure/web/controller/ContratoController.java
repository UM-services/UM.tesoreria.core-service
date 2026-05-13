package um.tesoreria.core.hexagonal.contrato.infrastructure.web.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import um.tesoreria.core.hexagonal.contrato.application.service.ContratoService;
import um.tesoreria.core.hexagonal.contrato.domain.model.Contrato;
import um.tesoreria.core.hexagonal.contrato.infrastructure.web.dto.ContratoRequest;
import um.tesoreria.core.hexagonal.contrato.infrastructure.web.dto.ContratoResponse;
import um.tesoreria.core.hexagonal.contrato.infrastructure.web.mapper.ContratoDtoMapper;

@RestController
@RequestMapping("/contrato")
@RequiredArgsConstructor
public class ContratoController {

    private final ContratoService contratoService;
    private final ContratoDtoMapper contratoDtoMapper;

    @GetMapping("/ajustable/{referencia}")
    public ResponseEntity<List<ContratoResponse>> findAllAjustable(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime referencia) {
        List<ContratoResponse> responses = contratoService.getContratosAjustables(referencia).stream()
                .map(contratoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/vigente/{referencia}")
    public ResponseEntity<List<ContratoResponse>> findAllVigente(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime referencia) {
        List<ContratoResponse> responses = contratoService.getContratosVigentes(referencia).stream()
                .map(contratoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<ContratoResponse>> findAllByPersona(
            @PathVariable BigDecimal personaId, @PathVariable Integer documentoId) {
        List<ContratoResponse> responses = contratoService.getContratosByPersona(personaId, documentoId).stream()
                .map(contratoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{contratoId}")
    public ResponseEntity<ContratoResponse> findByContratoId(@PathVariable Long contratoId) {
        return contratoService.getContratoById(contratoId)
                .map(contrato -> new ResponseEntity<>(contratoDtoMapper.toResponse(contrato), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{contratoId}")
    public ResponseEntity<ContratoResponse> update(@PathVariable Long contratoId, @RequestBody ContratoRequest request) {
        Contrato domain = contratoDtoMapper.toDomain(request);
        return contratoService.updateContrato(contratoId, domain)
                .map(updatedContrato -> new ResponseEntity<>(contratoDtoMapper.toResponse(updatedContrato), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/")
    public ResponseEntity<List<ContratoResponse>> saveAll(@RequestBody List<ContratoRequest> requests) {
        List<Contrato> domains = requests.stream()
                .map(contratoDtoMapper::toDomain)
                .collect(Collectors.toList());
        
        List<ContratoResponse> responses = contratoService.saveAllContratos(domains).stream()
                .map(contratoDtoMapper::toResponse)
                .collect(Collectors.toList());
        
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
