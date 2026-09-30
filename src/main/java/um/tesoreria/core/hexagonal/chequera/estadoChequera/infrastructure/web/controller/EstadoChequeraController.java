package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.application.service.EstadoChequeraService;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto.EstadoChequeraResponse;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.mapper.EstadoChequeraDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/chequera")
@RequiredArgsConstructor
public class EstadoChequeraController {

    private final EstadoChequeraService service;
    private final EstadoChequeraDtoMapper dtoMapper;

    @Operation(summary = "Estado de una chequera: datos del titular, cuotas por producto con sus subtotales y adhesión al débito automático. Lo consume report-service para generar el PDF \"Estado de Chequera\".")
    @GetMapping("/estado/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<EstadoChequeraResponse> getEstadoChequera(@PathVariable Integer facultadId,
                                                                    @PathVariable Integer tipoChequeraId,
                                                                    @PathVariable Long chequeraSerieId,
                                                                    @PathVariable Integer alternativaId) {
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(service.getEstadoChequera(facultadId, tipoChequeraId,
                    chequeraSerieId, alternativaId)));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}