package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.application.service.MovimientoConsultaService;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.AsientoDiarioResponse;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.LibroDiarioResponse;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto.RangoFechasRequest;
import um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.mapper.MovimientoConsultaDtoMapper;

import java.time.OffsetDateTime;

/** Consultas contables de solo lectura sobre los movimientos (`movcon`) de todas las cuentas. */
@RestController
@RequestMapping({"/movimientoConsulta", "/api/tesoreria/core/movimientoConsulta"})
@RequiredArgsConstructor
public class MovimientoConsultaController {

    private final MovimientoConsultaService service;
    private final MovimientoConsultaDtoMapper mapper;

    @PostMapping("/libroDiario")
    public ResponseEntity<LibroDiarioResponse> libroDiario(@RequestBody(required = false) RangoFechasRequest request) {
        var libro = request == null
                ? service.consultarLibroDiario(null, null)
                : service.consultarLibroDiario(request.desde(), request.hasta());
        return ResponseEntity.ok(mapper.toResponse(libro));
    }

    /** Un asiento con sus líneas; {@code fecha} con hora y offset, se toma su día de calendario. */
    @GetMapping("/asiento/{fecha}/{orden}")
    public ResponseEntity<AsientoDiarioResponse> asiento(
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime fecha,
            @PathVariable Integer orden) {
        return ResponseEntity.ok(mapper.toResponse(service.consultarAsiento(fecha, orden)));
    }
}
