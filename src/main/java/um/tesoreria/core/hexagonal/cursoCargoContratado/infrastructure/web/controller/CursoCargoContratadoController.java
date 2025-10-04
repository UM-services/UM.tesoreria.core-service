/**
 *
 */
package um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.web.controller;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import um.tesoreria.core.hexagonal.cursoCargoContratado.domain.ports.in.GetAllCargosByPersonaUseCase;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.web.dto.CursoCargoContratadoResponse;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.persistence.entity.CursoCargoContratadoEntity;
import um.tesoreria.core.hexagonal.cursoCargoContratado.application.service.CursoCargoContratadoService;
import um.tesoreria.core.hexagonal.cursoCargoContratado.infrastructure.web.mapper.CursoCargoContratadoDtoMapper;

/**
 * @author daniel
 */
@RestController
@RequestMapping({"/cursocargocontratado", "/api/core/cursocargocontratado"})
@Slf4j
@RequiredArgsConstructor
public class CursoCargoContratadoController {

    private final CursoCargoContratadoService service;

    private final GetAllCargosByPersonaUseCase getAllCargosByPersonaUseCase;
    private final CursoCargoContratadoDtoMapper cursoCargoContratadoDtoMapper;

    @GetMapping("/cursos/contrato/{contratoId}/periodo/{anho}/{mes}/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<CursoCargoContratadoResponse>> findAllByContrato(@PathVariable Long contratoId,
                                                                                @PathVariable Integer anho,
                                                                                @PathVariable Integer mes,
                                                                                @PathVariable BigDecimal personaId,
                                                                                @PathVariable Integer documentoId
    ) {
        List<CursoCargoContratadoResponse> productResponses = getAllCargosByPersonaUseCase
                .getAllCargosByPersona(contratoId, anho, mes, personaId, documentoId)
                .stream()
                .map(cursoCargoContratadoDtoMapper::toResponse)
                .collect(Collectors.toList());
        return new ResponseEntity<>(productResponses, HttpStatus.OK);
    }

    @GetMapping("/cursos/persona/{personaId}/{documentoId}/periodo/{anho}/{mes}")
    public ResponseEntity<List<CursoCargoContratadoEntity>> findAllByPersona(@PathVariable BigDecimal personaId,
                                                                             @PathVariable Integer documentoId,
                                                                             @PathVariable Integer anho,
                                                                             @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByPersona(personaId, documentoId, anho, mes));
    }

    @GetMapping("/curso/{cursoId}/{anho}/{mes}")
    public ResponseEntity<List<CursoCargoContratadoEntity>> findAllByCurso(@PathVariable Long cursoId,
                                                                           @PathVariable Integer anho,
                                                                           @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByCurso(cursoId, anho, mes));
    }

    @GetMapping("/{cursocargocontratadoId}")
    public ResponseEntity<CursoCargoContratadoEntity> findByCursoCargo(@PathVariable Long cursocargocontratadoId) {
        return ResponseEntity.ok(service.findByCursoCargo(cursocargocontratadoId));
    }

    @PostMapping("/")
    public ResponseEntity<CursoCargoContratadoEntity> add(@RequestBody CursoCargoContratadoEntity cursocargocontratado) {
        return ResponseEntity.ok(service.add(cursocargocontratado));
    }

    @PutMapping("/{cursoCargoContratadoId}")
    public ResponseEntity<CursoCargoContratadoEntity> update(@RequestBody CursoCargoContratadoEntity cursoCargoContratadoEntity,
                                                             @PathVariable Long cursoCargoContratadoId) {
        return ResponseEntity.ok(service.update(cursoCargoContratadoEntity, cursoCargoContratadoId));
    }

    @DeleteMapping("/{cursoCargoContratadoId}")
    public ResponseEntity<Void> delete(@PathVariable Long cursoCargoContratadoId) {
        service.delete(cursoCargoContratadoId);
        return ResponseEntity.noContent().build();
    }

}
