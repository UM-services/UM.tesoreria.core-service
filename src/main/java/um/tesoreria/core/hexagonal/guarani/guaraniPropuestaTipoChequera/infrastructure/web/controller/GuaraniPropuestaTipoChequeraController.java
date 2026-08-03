package um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.exception.GuaraniPropuestaTipoChequeraException;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.application.service.GuaraniPropuestaTipoChequeraService;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.domain.model.GuaraniPropuestaTipoChequera;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.dto.GuaraniPropuestaTipoChequeraRequest;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.dto.GuaraniPropuestaTipoChequeraResponse;
import um.tesoreria.core.hexagonal.guarani.guaraniPropuestaTipoChequera.infrastructure.web.mapper.GuaraniPropuestaTipoChequeraDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/guaraniPropuestaTipoChequera")
@RequiredArgsConstructor
public class GuaraniPropuestaTipoChequeraController {
    private final GuaraniPropuestaTipoChequeraService service;
    private final GuaraniPropuestaTipoChequeraDtoMapper dtoMapper;

    @GetMapping("/propuesta/{propuestaGuarani}/lectivo/{lectivoId}")
    public ResponseEntity<GuaraniPropuestaTipoChequeraResponse> findByPropuestaAndLectivo(
            @PathVariable Integer propuestaGuarani, @PathVariable Integer lectivoId) {
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(
                    service.findByPropuestaAndLectivo(propuestaGuarani, lectivoId)));
        } catch (GuaraniPropuestaTipoChequeraException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public ResponseEntity<GuaraniPropuestaTipoChequeraResponse> add(
            @RequestBody GuaraniPropuestaTipoChequeraRequest request) {
        GuaraniPropuestaTipoChequera created = service.create(dtoMapper.toDomain(request));
        return ResponseEntity.ok(dtoMapper.toResponse(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuaraniPropuestaTipoChequeraResponse> update(
            @PathVariable Integer id, @RequestBody GuaraniPropuestaTipoChequeraRequest request) {
        GuaraniPropuestaTipoChequera domain = dtoMapper.toDomain(request);
        domain.setGuaraniPropuestaTipoChequeraId(id);
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(service.update(domain)));
        } catch (GuaraniPropuestaTipoChequeraException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            service.delete(id);
            return ResponseEntity.noContent().build();
        } catch (GuaraniPropuestaTipoChequeraException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
