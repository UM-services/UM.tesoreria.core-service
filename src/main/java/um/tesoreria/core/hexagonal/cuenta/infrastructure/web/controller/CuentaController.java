package um.tesoreria.core.hexagonal.cuenta.infrastructure.web.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.hexagonal.cuenta.application.service.CuentaService;
import um.tesoreria.core.hexagonal.cuenta.domain.model.Cuenta;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.web.dto.*;
import um.tesoreria.core.hexagonal.cuenta.infrastructure.web.mapper.CuentaDtoMapper;

@RestController
@RequestMapping({"/cuenta", "/api/tesoreria/core/cuenta"})
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService service;
    private final CuentaDtoMapper mapper;

    @GetMapping("/")
    public ResponseEntity<List<CuentaResponse>> findAll() {
        return ResponseEntity.ok(service.findAll().stream().map(mapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/cierreresultado")
    public ResponseEntity<List<CuentaResponse>> findAllByCierreResultado() {
        return ResponseEntity.ok(service.findAllByCierreResultado().stream().map(mapper::toResponse).collect(Collectors.toList()));
    }

    @GetMapping("/cierreactivopasivo")
    public ResponseEntity<List<CuentaResponse>> findAllByCierreActivoPasivo() {
        return ResponseEntity.ok(service.findAllByCierreActivoPasivo().stream().map(mapper::toResponse).collect(Collectors.toList()));
    }

    @PostMapping("/search/{visible}")
    public ResponseEntity<List<CuentaSearchResponse>> findByStrings(@RequestBody List<String> conditions, @PathVariable Boolean visible) {
        return ResponseEntity.ok(service.findByStrings(conditions, visible).stream().map(mapper::toSearchResponse).collect(Collectors.toList()));
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> findByCuenta(@PathVariable BigDecimal numeroCuenta) {
        return service.findByNumeroCuenta(numeroCuenta)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
    }

    @GetMapping("/id/{cuentaContableId}")
    public ResponseEntity<CuentaResponse> findByCuentaContableId(@PathVariable Long cuentaContableId) {
        return service.findByCuentaContableId(cuentaContableId)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
    }

    @PostMapping("/")
    public ResponseEntity<CuentaResponse> add(@RequestBody CuentaRequest request) {
        Cuenta cuenta = mapper.toDomain(request);
        return ResponseEntity.ok(mapper.toResponse(service.add(cuenta)));
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> update(@RequestBody CuentaRequest request, @PathVariable BigDecimal numeroCuenta) {
        Cuenta cuenta = mapper.toDomain(request);
        return service.update(cuenta, numeroCuenta)
                .map(mapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
    }

    @DeleteMapping("/{numeroCuenta}")
    public ResponseEntity<Void> delete(@PathVariable BigDecimal numeroCuenta) {
        service.delete(numeroCuenta);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/recalculagrados")
    public ResponseEntity<String> recalculaGrados() {
        return new ResponseEntity<>(service.recalculaGrados(), HttpStatus.OK);
    }

}