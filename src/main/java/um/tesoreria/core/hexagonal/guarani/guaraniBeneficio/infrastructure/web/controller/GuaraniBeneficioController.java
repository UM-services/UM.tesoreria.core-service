package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
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

@RestController
@RequestMapping("/api/tesoreria/core/guaraniBeneficio")
@RequiredArgsConstructor
public class GuaraniBeneficioController {

    private final GuaraniBeneficioService guaraniBeneficioService;
    private final GuaraniBeneficioDtoMapper guaraniBeneficioDtoMapper;

    @GetMapping("/requisito/{requisito}")
    public ResponseEntity<GuaraniBeneficioResponse> findByRequisito(@PathVariable Integer requisito) {
        try {
            var domain = guaraniBeneficioService.findByRequisito(requisito);
            return ResponseEntity.ok(guaraniBeneficioDtoMapper.toResponse(domain));
        } catch (GuaraniBeneficioException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<GuaraniBeneficioResponse> add(@RequestBody GuaraniBeneficioRequest request) {
        GuaraniBeneficio domain = guaraniBeneficioDtoMapper.toDomain(request);
        GuaraniBeneficio created = guaraniBeneficioService.create(domain);
        return new ResponseEntity<>(guaraniBeneficioDtoMapper.toResponse(created), HttpStatus.OK);
    }

    @PutMapping("/requisito/{requisito}")
    public ResponseEntity<GuaraniBeneficioResponse> updateByRequisito(
            @PathVariable Integer requisito,
            @RequestBody GuaraniBeneficioRequest request) {
        GuaraniBeneficio domain = guaraniBeneficioDtoMapper.toDomain(request);
        GuaraniBeneficio updated = guaraniBeneficioService.updateByRequisito(requisito, domain);
        return new ResponseEntity<>(guaraniBeneficioDtoMapper.toResponse(updated), HttpStatus.OK);
    }
}
