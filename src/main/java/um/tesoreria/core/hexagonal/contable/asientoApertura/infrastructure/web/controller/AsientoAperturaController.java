package um.tesoreria.core.hexagonal.contable.asientoApertura.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableResponse;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.mapper.AsientoContableDtoMapper;
import um.tesoreria.core.hexagonal.contable.asientoApertura.application.exception.AsientoAperturaException;
import um.tesoreria.core.hexagonal.contable.asientoApertura.domain.ports.in.AsientoAperturaUseCase;
import um.tesoreria.core.hexagonal.contable.asientoApertura.infrastructure.web.dto.AperturaPreviewRequest;
import um.tesoreria.core.hexagonal.contable.asientoApertura.infrastructure.web.dto.AperturaRequest;

import java.util.Map;

@RestController
@RequestMapping({"/asientoApertura", "/api/tesoreria/core/asientoApertura"})
@RequiredArgsConstructor
public class AsientoAperturaController {

    private final AsientoAperturaUseCase service;
    private final AsientoContableDtoMapper mapper;

    @ExceptionHandler(AsientoAperturaException.class)
    public ResponseEntity<Map<String, Object>> error(AsientoAperturaException ex) {
        return ResponseEntity.status(ex.getCodigo().status()).body(Map.of(
                "status", ex.getCodigo().status().value(),
                "error", ex.getCodigo().name(),
                "message", ex.getMessage()));
    }

    @PostMapping("/{ejercicioId}/preview")
    @RequierePermiso("contable.asiento_apertura.calcular")
    public ResponseEntity<AsientoContableResponse> preview(@PathVariable Integer ejercicioId,
                                                           @RequestBody AperturaPreviewRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.preview(ejercicioId, request.referencia())));
    }

    @PostMapping("/{ejercicioId}")
    @RequierePermiso("contable.asiento_apertura.generar")
    public ResponseEntity<AsientoContableResponse> registrar(@PathVariable Integer ejercicioId,
                                                             @RequestBody AperturaRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.registrar(
                ejercicioId, request.concepto(), mapper.toLineas(request.lineas()))));
    }

}
