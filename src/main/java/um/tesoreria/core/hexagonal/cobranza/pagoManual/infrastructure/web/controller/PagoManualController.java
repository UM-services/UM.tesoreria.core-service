package um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualNotFoundException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.SolicitudPagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in.EliminarPagoManualUseCase;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in.RegistrarPagoManualUseCase;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto.PagoManualRequest;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.dto.PagoManualResponse;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.mapper.PagoManualDtoMapper;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/api/tesoreria/core/cobranza/pago-manual")
@RequiredArgsConstructor
public class PagoManualController {

    private final RegistrarPagoManualUseCase registrarPagoManualUseCase;
    private final EliminarPagoManualUseCase eliminarPagoManualUseCase;
    private final PagoManualDtoMapper mapper;

    @PostMapping
    public ResponseEntity<PagoManualResponse> registrar(@RequestBody PagoManualRequest request) {
        try {
            SolicitudPagoManual solicitud = new SolicitudPagoManual(request.chequeraCuotaId(),
                    fecha(request.fecha()), request.importe(), request.observaciones(),
                    request.geograficaId());
            return ResponseEntity.ok(mapper.toResponse(registrarPagoManualUseCase.registrar(solicitud)));
        } catch (PagoManualNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (PagoManualException e) {
            throw new ResponseStatusException(e.isConflict() ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }

    @DeleteMapping("/{chequeraPagoId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long chequeraPagoId) {
        try {
            eliminarPagoManualUseCase.eliminar(chequeraPagoId);
            return ResponseEntity.noContent().build();
        } catch (PagoManualNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        } catch (PagoManualException e) {
            throw new ResponseStatusException(e.isConflict() ? HttpStatus.CONFLICT : HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }
    }

    private OffsetDateTime fecha(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value.trim());
        } catch (RuntimeException e) {
            try {
                return LocalDate.parse(value.trim()).atStartOfDay().atOffset(ZoneOffset.UTC);
            } catch (RuntimeException e2) {
                throw new PagoManualException("Fecha inválida: " + value);
            }
        }
    }

}
