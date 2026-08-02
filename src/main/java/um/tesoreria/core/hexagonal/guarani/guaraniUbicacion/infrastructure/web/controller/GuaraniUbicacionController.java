package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.controller;

import java.util.List;
import java.util.stream.Collectors;
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
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.exception.GuaraniUbicacionException;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.service.GuaraniUbicacionService;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.dto.GuaraniUbicacionRequest;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.dto.GuaraniUbicacionResponse;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.mapper.GuaraniUbicacionDtoMapper;

@RestController
@RequestMapping("/api/tesoreria/core/guaraniUbicacion")
@RequiredArgsConstructor
public class GuaraniUbicacionController {
    private final GuaraniUbicacionService guaraniUbicacionService;
    private final GuaraniUbicacionDtoMapper guaraniUbicacionDtoMapper;

    @GetMapping({"", "/"})
    public ResponseEntity<List<GuaraniUbicacionResponse>> findAll() {
        List<GuaraniUbicacionResponse> responses = guaraniUbicacionService.findAll().stream()
                .map(guaraniUbicacionDtoMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GuaraniUbicacionResponse> findById(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(guaraniUbicacionDtoMapper.toResponse(guaraniUbicacionService.findById(id)));
        } catch (GuaraniUbicacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @PostMapping({"", "/"})
    public ResponseEntity<GuaraniUbicacionResponse> add(@RequestBody GuaraniUbicacionRequest request) {
        try {
            GuaraniUbicacion created = guaraniUbicacionService.create(guaraniUbicacionDtoMapper.toDomain(request));
            return ResponseEntity.ok(guaraniUbicacionDtoMapper.toResponse(created));
        } catch (GuaraniUbicacionException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<GuaraniUbicacionResponse> update(
            @PathVariable Integer id, @RequestBody GuaraniUbicacionRequest request) {
        GuaraniUbicacion domain = guaraniUbicacionDtoMapper.toDomain(request);
        domain.setGuaraniUbicacionId(id);
        try {
            return ResponseEntity.ok(guaraniUbicacionDtoMapper.toResponse(guaraniUbicacionService.update(domain)));
        } catch (GuaraniUbicacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            guaraniUbicacionService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (GuaraniUbicacionException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
        }
    }
}
