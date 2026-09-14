package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception.GuaraniBeneficioException;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.service.GuaraniBeneficioService;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.domain.model.GuaraniBeneficio;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto.GuaraniBeneficioRequest;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.dto.GuaraniBeneficioResponse;
import um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.mapper.GuaraniBeneficioDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tesoreria/core/guaraniBeneficio")
@RequiredArgsConstructor
public class GuaraniBeneficioController {

    private final GuaraniBeneficioService guaraniBeneficioService;
    private final GuaraniBeneficioDtoMapper guaraniBeneficioDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<GuaraniBeneficioResponse>> findAll() {
        List<GuaraniBeneficioResponse> responses = guaraniBeneficioService.findAll().stream()
                .map(guaraniBeneficioDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/requisito/{requisito}")
    public ResponseEntity<GuaraniBeneficioResponse> findByRequisito(@PathVariable Integer requisito) {
        try {
            var domain = guaraniBeneficioService.findByRequisito(requisito);
            return ResponseEntity.ok(guaraniBeneficioDtoMapper.toResponse(domain));
        } catch (GuaraniBeneficioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/requisitos")
    public ResponseEntity<List<GuaraniBeneficioResponse>> findByRequisitos(
            @RequestBody List<Integer> requisito) {
        List<GuaraniBeneficioResponse> responses = guaraniBeneficioService.findByRequisitos(requisito).stream()
                .map(guaraniBeneficioDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/")
    public ResponseEntity<GuaraniBeneficioResponse> add(@Valid @RequestBody GuaraniBeneficioRequest request) {
        try {
            GuaraniBeneficio domain = guaraniBeneficioDtoMapper.toDomain(request);
            GuaraniBeneficio created = guaraniBeneficioService.create(domain);
            return ResponseEntity.ok(guaraniBeneficioDtoMapper.toResponse(created));
        } catch (GuaraniBeneficioException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/requisito/{requisito}")
    public ResponseEntity<GuaraniBeneficioResponse> updateByRequisito(
            @PathVariable Integer requisito,
            @Valid @RequestBody GuaraniBeneficioRequest request) {
        GuaraniBeneficio domain = guaraniBeneficioDtoMapper.toDomain(request);
        GuaraniBeneficio updated = guaraniBeneficioService.updateByRequisito(requisito, domain);
        return ResponseEntity.ok(guaraniBeneficioDtoMapper.toResponse(updated));
    }
}
