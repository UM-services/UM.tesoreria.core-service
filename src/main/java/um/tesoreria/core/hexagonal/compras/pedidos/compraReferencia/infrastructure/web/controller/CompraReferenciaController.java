package um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.exception.CompraReferenciaException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.application.service.CompraReferenciaService;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.domain.model.CompraReferencia;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.dto.CompraReferenciaResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraReferencia.infrastructure.web.mapper.CompraReferenciaDtoMapper;

/**
 * Valor de referencia por ejercicio (unidad del múltiplo de autorización por monto).
 */
@RestController
@RequestMapping("/api/tesoreria/core/compraReferencia")
@RequiredArgsConstructor
public class CompraReferenciaController {

    private final CompraReferenciaService compraReferenciaService;
    private final CompraReferenciaDtoMapper compraReferenciaDtoMapper;

    @GetMapping("/{ejercicioId}")
    public ResponseEntity<CompraReferenciaResponse> get(@PathVariable Integer ejercicioId) {
        return compraReferenciaService.getByEjercicioId(ejercicioId)
                .map(compraReferenciaDtoMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Referencia no encontrada para el ejercicio " + ejercicioId));
    }

    @PutMapping("/{ejercicioId}")
    public ResponseEntity<CompraReferenciaResponse> upsert(@PathVariable Integer ejercicioId,
                                                           @Valid @RequestBody CompraReferenciaRequest request) {
        try {
            CompraReferencia referencia = compraReferenciaDtoMapper.toDomain(ejercicioId, request);
            return ResponseEntity.ok(compraReferenciaDtoMapper.toResponse(compraReferenciaService.upsert(referencia)));
        } catch (CompraReferenciaException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

}
