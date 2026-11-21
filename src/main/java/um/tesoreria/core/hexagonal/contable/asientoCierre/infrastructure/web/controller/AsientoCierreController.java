package um.tesoreria.core.hexagonal.contable.asientoCierre.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableResponse;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.mapper.AsientoContableDtoMapper;
import um.tesoreria.core.hexagonal.contable.asientoCierre.application.exception.AsientoCierreException;
import um.tesoreria.core.hexagonal.contable.asientoCierre.domain.model.VarianteCierre;
import um.tesoreria.core.hexagonal.contable.asientoCierre.domain.ports.in.AsientoCierreUseCase;
import um.tesoreria.core.hexagonal.contable.asientoCierre.infrastructure.web.dto.CierreRequest;

import java.util.Map;

@RestController
@RequestMapping({"/asientoCierre", "/api/tesoreria/core/asientoCierre"})
@RequiredArgsConstructor
public class AsientoCierreController {

    private final AsientoCierreUseCase service;
    private final AsientoContableDtoMapper mapper;

    @ExceptionHandler(AsientoCierreException.class)
    public ResponseEntity<Map<String, Object>> error(AsientoCierreException ex) {
        return ResponseEntity.status(ex.getCodigo().status()).body(Map.of(
                "status", ex.getCodigo().status().value(),
                "error", ex.getCodigo().name(),
                "message", ex.getMessage()));
    }

    @GetMapping("/{ejercicioId}/resultado/preview")
    @RequierePermiso("contable.asiento_cierre.calcular")
    public ResponseEntity<AsientoContableResponse> previewResultado(@PathVariable Integer ejercicioId) {
        return ResponseEntity.ok(mapper.toResponse(service.preview(ejercicioId, VarianteCierre.RESULTADO)));
    }

    @PostMapping("/{ejercicioId}/resultado")
    @RequierePermiso("contable.asiento_cierre.generar")
    public ResponseEntity<AsientoContableResponse> registrarResultado(@PathVariable Integer ejercicioId,
                                                                      @RequestBody CierreRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.registrar(ejercicioId, VarianteCierre.RESULTADO,
                request.concepto(), mapper.toLineas(request.lineas()))));
    }

    @GetMapping("/{ejercicioId}/activo-pasivo/preview")
    @RequierePermiso("contable.asiento_cierre.calcular")
    public ResponseEntity<AsientoContableResponse> previewActivoPasivo(@PathVariable Integer ejercicioId) {
        return ResponseEntity.ok(mapper.toResponse(service.preview(ejercicioId, VarianteCierre.ACTIVO_PASIVO)));
    }

    @PostMapping("/{ejercicioId}/activo-pasivo")
    @RequierePermiso("contable.asiento_cierre.generar")
    public ResponseEntity<AsientoContableResponse> registrarActivoPasivo(@PathVariable Integer ejercicioId,
                                                                         @RequestBody CierreRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.registrar(ejercicioId, VarianteCierre.ACTIVO_PASIVO,
                request.concepto(), mapper.toLineas(request.lineas()))));
    }

}
