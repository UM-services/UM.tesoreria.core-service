package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.application.service.ReservaVacanteService;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteResponse;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.mapper.ReservaVacanteDtoMapper;

import java.util.UUID;

@RestController
@RequestMapping("/api/tesoreria/core/umhub/reservaVacante")
@RequiredArgsConstructor
public class ReservaVacanteController {

    private final ReservaVacanteService reservaVacanteService;
    private final ReservaVacanteDtoMapper reservaVacanteDtoMapper;

    @PostMapping("/")
    public ResponseEntity<ReservaVacanteResponse> add(@RequestBody ReservaVacanteRequest request) {
        ReservaVacante created = reservaVacanteService.createReservaVacante(request);
        return new ResponseEntity<>(reservaVacanteDtoMapper.toResponse(created), HttpStatus.CREATED);
    }

    @GetMapping("/{reservaVacanteId}")
    public ResponseEntity<ReservaVacanteResponse> get(@PathVariable UUID reservaVacanteId) {
        ReservaVacante reservaVacante = reservaVacanteService.findReservaVacante(reservaVacanteId);
        return ResponseEntity.ok(reservaVacanteDtoMapper.toResponse(reservaVacante));
    }

    /**
     * Claim atómico de la notificación de pago. {@code true} = esta instancia debe notificar;
     * {@code false} = otra entrega del evento ya la reclamó (no-op idempotente).
     */
    @PostMapping("/{reservaVacanteId}/notificacion/claim")
    public ResponseEntity<Boolean> claimNotificacionPago(@PathVariable UUID reservaVacanteId) {
        return ResponseEntity.ok(reservaVacanteService.reclamarNotificacionPago(reservaVacanteId));
    }

    /**
     * El envío a n8n falló: se libera la marca para que el reintento de Kafka pueda reclamarla.
     */
    @PostMapping("/{reservaVacanteId}/notificacion/release")
    public ResponseEntity<Void> releaseNotificacionPago(@PathVariable UUID reservaVacanteId) {
        reservaVacanteService.liberarNotificacionPago(reservaVacanteId);
        return ResponseEntity.ok().build();
    }

}
