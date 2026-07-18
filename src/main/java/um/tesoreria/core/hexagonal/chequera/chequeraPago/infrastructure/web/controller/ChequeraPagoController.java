package um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception.ChequeraPagoException;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.application.service.ChequeraPagoService;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.dto.ChequeraPagoRequest;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.dto.ChequeraPagoResponse;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.infrastructure.web.mapper.ChequeraPagoDtoMapper;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/chequeraPago", "/api/tesoreria/core/chequeraPago"})
@RequiredArgsConstructor
public class ChequeraPagoController {

    private final ChequeraPagoService chequeraPagoService;
    private final ChequeraPagoDtoMapper chequeraPagoDtoMapper;

    @GetMapping("/pendientesFactura/{fechaPago}")
    public ResponseEntity<List<ChequeraPagoResponse>> pendientesFactura(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaPago) {
        List<ChequeraPagoResponse> responses = chequeraPagoService.pendientesFactura(fechaPago).stream()
                .map(chequeraPagoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<ChequeraPagoResponse>> findAllByChequera(
            @PathVariable Integer facultadId,
            @PathVariable Integer tipoChequeraId,
            @PathVariable Long chequeraSerieId) {
        List<ChequeraPagoResponse> responses = chequeraPagoService
                .findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId).stream()
                .map(chequeraPagoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fecha/acreditacion/{tipoPagoId}/{fechaAcreditacion}")
    public ResponseEntity<List<ChequeraPagoResponse>> findAllByTipoPagoIdAndFechaAcreditacion(
            @PathVariable Integer tipoPagoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaAcreditacion) {
        List<ChequeraPagoResponse> responses = chequeraPagoService
                .findAllByTipoPagoIdAndFechaAcreditacion(tipoPagoId, fechaAcreditacion).stream()
                .map(chequeraPagoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/fecha/pago/{tipoPagoId}/{fechaPago}")
    public ResponseEntity<List<ChequeraPagoResponse>> findAllByTipoPagoIdAndFechaPago(
            @PathVariable Integer tipoPagoId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaPago) {
        List<ChequeraPagoResponse> responses = chequeraPagoService
                .findAllByTipoPagoIdAndFechaPago(tipoPagoId, fechaPago).stream()
                .map(chequeraPagoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pagos/facultad/{facultadId}/tipoChequera/{tipoChequeraId}/lectivo/{lectivoId}")
    public ResponseEntity<List<ChequeraPagoResponse>> findAllByFacultadIdAndTipoChequeraIdAndLectivoId(
            @PathVariable Integer facultadId,
            @PathVariable Integer tipoChequeraId,
            @PathVariable Integer lectivoId) {
        List<ChequeraPagoResponse> responses = chequeraPagoService
                .findAllByFacultadIdAndTipoChequeraIdAndLectivoId(facultadId, tipoChequeraId, lectivoId).stream()
                .map(chequeraPagoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{chequeraPagoId}")
    public ResponseEntity<ChequeraPagoResponse> findByChequeraPagoId(@PathVariable Long chequeraPagoId) {
        try {
            ChequeraPago domain = chequeraPagoService.findByChequeraPagoId(chequeraPagoId);
            return ResponseEntity.ok(chequeraPagoDtoMapper.toResponse(domain));
        } catch (ChequeraPagoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraPagoResponse> add(@RequestBody ChequeraPagoRequest request) {
        ChequeraPago domain = chequeraPagoDtoMapper.toDomain(request);
        ChequeraPago created = chequeraPagoService.create(domain);
        return ResponseEntity.ok(chequeraPagoDtoMapper.toResponse(created));
    }

    @GetMapping("/search/id/mercado/pago/{idMercadoPago}")
    public ResponseEntity<ChequeraPagoResponse> findByIdMercadoPago(@PathVariable String idMercadoPago) {
        try {
            ChequeraPago domain = chequeraPagoService.findByIdMercadoPago(idMercadoPago);
            return ResponseEntity.ok(chequeraPagoDtoMapper.toResponse(domain));
        } catch (ChequeraPagoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

}
