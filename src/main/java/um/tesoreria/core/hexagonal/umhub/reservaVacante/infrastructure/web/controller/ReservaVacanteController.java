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

}
