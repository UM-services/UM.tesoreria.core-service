package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.configuration.security.RequierePermiso;
import um.tesoreria.core.configuration.security.RequierePermisoInterceptor;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service.AsientoIngresoService;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoJobDetalleResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoJobResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoResumenResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto.AsientoIngresoResultadoResponse;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.mapper.AsientoIngresoDtoMapper;

import java.time.OffsetDateTime;

@RestController
@RequestMapping({"/asientoIngreso", "/api/tesoreria/core/asientoIngreso"})
@RequiredArgsConstructor
@Slf4j
public class AsientoIngresoController {

    private final AsientoIngresoService service;
    private final AsientoIngresoDtoMapper mapper;

    @GetMapping("/{fecha}/{tipoPagoId}")
    @RequierePermiso("contable.asiento_ingreso.consultar")
    public ResponseEntity<AsientoIngresoResumenResponse> consultar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha,
            @PathVariable Integer tipoPagoId) {
        return ResponseEntity.ok(mapper.toResponse(service.consultar(fecha, tipoPagoId)));
    }

    @PostMapping("/{fecha}/{tipoPagoId}/regenerar")
    @RequierePermiso("contable.asiento_ingreso.regenerar")
    public ResponseEntity<AsientoIngresoResultadoResponse> regenerar(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha,
            @PathVariable Integer tipoPagoId) {
        return ResponseEntity.ok(mapper.toResponse(service.regenerar(fecha, tipoPagoId)));
    }

    @PostMapping("/masivo/{fechaDesde}/{fechaHasta}")
    @RequierePermiso("contable.asiento_ingreso.masivo")
    public ResponseEntity<AsientoIngresoJobResponse> iniciarMasivo(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaDesde,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaHasta,
            HttpServletRequest request) {
        var job = service.iniciarMasiva(fechaDesde, fechaHasta, usuarioId(request));
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(mapper.toResponse(job));
    }

    @GetMapping("/masivo/{jobId}")
    @RequierePermiso("contable.asiento_ingreso.consultar")
    public ResponseEntity<AsientoIngresoJobDetalleResponse> consultarJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(mapper.toResponse(service.consultarJob(jobId)));
    }

    @PostMapping("/masivo/{jobId}/cancelar")
    @RequierePermiso("contable.asiento_ingreso.masivo")
    public ResponseEntity<AsientoIngresoJobResponse> cancelarJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(mapper.toResponse(service.cancelarJob(jobId)));
    }

    /** Identidad transitoria hasta que exista JWT (mismo header que el interceptor de permisos). */
    private Long usuarioId(HttpServletRequest request) {
        String header = request.getHeader(RequierePermisoInterceptor.USER_ID_HEADER);
        if (header == null || header.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(header.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
