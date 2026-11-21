package um.tesoreria.core.hexagonal.contable.asientoManual.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaResponse;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableResponse;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.mapper.AsientoContableDtoMapper;
import um.tesoreria.core.hexagonal.contable.asientoManual.application.exception.AsientoManualException;
import um.tesoreria.core.hexagonal.contable.asientoManual.application.exception.AsientoManualException.Codigo;
import um.tesoreria.core.hexagonal.contable.asientoManual.domain.ports.in.AsientoManualUseCase;
import um.tesoreria.core.hexagonal.contable.asientoManual.infrastructure.web.dto.AsientoManualRequest;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.AsientoConflictoException;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/asientoManual", "/api/tesoreria/core/asientoManual"})
@RequiredArgsConstructor
public class AsientoManualController {

    private final AsientoManualUseCase service;
    private final AsientoContableDtoMapper mapper;

    @ExceptionHandler(AsientoManualException.class)
    public ResponseEntity<Map<String, Object>> error(AsientoManualException ex) {
        return ResponseEntity.status(ex.getCodigo().status()).body(Map.of(
                "status", ex.getCodigo().status().value(),
                "error", ex.getCodigo().name(),
                "message", ex.getMessage()));
    }

    @ExceptionHandler(AsientoConflictoException.class)
    public ResponseEntity<Map<String, Object>> conflicto(AsientoConflictoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of(
                "status", 409,
                "error", "CONFLICTO",
                "message", ex.getMessage()));
    }

    @PostMapping
    @RequierePermiso("contable.asiento_manual.crear")
    public ResponseEntity<AsientoContableResponse> registrar(@RequestBody AsientoManualRequest request) {
        return ResponseEntity.ok(mapper.toResponse(service.registrar(
                request.fecha(), request.numero(), request.concepto(), mapper.toLineas(request.lineas()))));
    }

    @GetMapping("/{fecha}/{orden}")
    @RequierePermiso("contable.asiento_manual.consultar")
    public ResponseEntity<AsientoContableResponse> consultar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha,
            @PathVariable Integer orden) {
        return ResponseEntity.ok(mapper.toResponse(service.consultar(fecha, orden)));
    }

    @DeleteMapping("/{fecha}/{orden}")
    @RequierePermiso("contable.asiento_manual.eliminar")
    public ResponseEntity<Void> eliminar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha,
            @PathVariable Integer orden) {
        service.eliminar(fecha, orden);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/importar")
    @RequierePermiso("contable.asiento_manual.importar")
    public ResponseEntity<List<AsientoContableLineaResponse>> importar(@RequestPart("archivo") MultipartFile archivo) {
        try {
            return ResponseEntity.ok(service.importar(archivo.getInputStream()).stream()
                    .map(mapper::toLineaResponse)
                    .toList());
        } catch (IOException e) {
            throw new AsientoManualException(Codigo.IMPORTACION_INVALIDA,
                    "No se pudo leer el archivo " + archivo.getOriginalFilename(), e);
        }
    }

}
