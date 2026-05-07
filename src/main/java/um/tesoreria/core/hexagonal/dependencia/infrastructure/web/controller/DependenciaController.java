/**
 *
 */
package um.tesoreria.core.hexagonal.dependencia.infrastructure.web.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import um.tesoreria.core.hexagonal.dependencia.infrastructure.web.dto.DependenciaRequest;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.web.dto.DependenciaResponse;
import um.tesoreria.core.hexagonal.dependencia.infrastructure.web.mapper.DependenciaDtoMapper;
import um.tesoreria.core.hexagonal.dependencia.domain.model.Dependencia;
import um.tesoreria.core.hexagonal.dependencia.application.service.DependenciaService;

/**
 * @author daniel
 */
@RestController
@RequiredArgsConstructor
@RequestMapping({"/dependencia", "/api/tesoreria/core/dependencia"})
public class DependenciaController {

    private final DependenciaService service;
    private final DependenciaDtoMapper mapper;

    @GetMapping("/")
    public ResponseEntity<List<DependenciaResponse>> findAll() {
        List<DependenciaResponse> responses = service.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @GetMapping("/{dependenciaId}")
    public ResponseEntity<DependenciaResponse> findByDependenciaId(@PathVariable Integer dependenciaId) {
        Dependencia domain = service.findByDependenciaId(dependenciaId);
        return new ResponseEntity<>(mapper.toResponse(domain), HttpStatus.OK);
    }

    @PutMapping("/{dependenciaId}")
    public ResponseEntity<DependenciaResponse> update(@PathVariable Integer dependenciaId, @RequestBody DependenciaRequest request) {
        Dependencia domain = mapper.toDomain(request);
        Dependencia updated = service.update(dependenciaId, domain);
        return new ResponseEntity<>(mapper.toResponse(updated), HttpStatus.OK);
    }

}
