package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.core.JsonProcessingException;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.exception.CuentaMovimientoException;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.service.CuentaMovimientoService;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.dto.CuentaMovimientoRequest;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.dto.CuentaMovimientoResponse;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.infrastructure.web.mapper.CuentaMovimientoDtoMapper;
import um.tesoreria.core.model.view.CuentaMovimientoAsiento;

@RestController
@RequestMapping({"/cuentaMovimiento", "/api/tesoreria/core/cuentaMovimiento"})
@RequiredArgsConstructor
public class CuentaMovimientoController {

    private final CuentaMovimientoService cuentaMovimientoService;
    private final CuentaMovimientoDtoMapper cuentaMovimientoDtoMapper;

    @GetMapping("/cuenta/{numeroCuenta}/{onlyOne}")
    public ResponseEntity<List<CuentaMovimientoResponse>> findAllByNumeroCuenta(
            @PathVariable BigDecimal numeroCuenta, @PathVariable Boolean onlyOne) {
        List<CuentaMovimientoResponse> responses = cuentaMovimientoService.findAllByNumeroCuenta(numeroCuenta, onlyOne).stream()
                .map(cuentaMovimientoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/asiento/{fechaContable}/{ordenContable}")
    public ResponseEntity<List<CuentaMovimientoResponse>> findAllByAsiento(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fechaContable,
            @PathVariable Integer ordenContable) {
        List<CuentaMovimientoResponse> responses = cuentaMovimientoService.findAllByAsiento(fechaContable, ordenContable, 0, 2).stream()
                .map(cuentaMovimientoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/entregaDetalle/{proveedorMovimientoId}")
    public ResponseEntity<List<CuentaMovimientoAsiento>> findAllEntregaDetalleByProveedorMovimientoId(
            @PathVariable Long proveedorMovimientoId) throws JsonProcessingException {
        return ResponseEntity.ok(cuentaMovimientoService.findAllEntregaDetalleByProveedorMovimientoId(proveedorMovimientoId));
    }

    @PostMapping("/entregaDetalle")
    public ResponseEntity<List<CuentaMovimientoAsiento>> findAllEntregaDetalleByProveedorMovimientoIds(
            @RequestBody List<Long> proveedorMovimientoIds) throws JsonProcessingException {
        return ResponseEntity.ok(cuentaMovimientoService.findAllEntregaDetalleByProveedorMovimientoIds(proveedorMovimientoIds));
    }

    @GetMapping("/{cuentaMovimientoId}")
    public ResponseEntity<CuentaMovimientoResponse> findByCuentaMovimientoId(@PathVariable Long cuentaMovimientoId) {
        try {
            var domain = cuentaMovimientoService.findByCuentaMovimientoId(cuentaMovimientoId);
            return ResponseEntity.ok(cuentaMovimientoDtoMapper.toResponse(domain));
        } catch (CuentaMovimientoException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
