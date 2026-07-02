package um.tesoreria.core.hexagonal.lectivo.infrastructure.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.lectivo.application.exception.LectivoException;
import um.tesoreria.core.hexagonal.lectivo.application.service.LectivoService;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.web.dto.LectivoRequest;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.web.dto.LectivoResponse;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.web.mapper.LectivoDtoMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping({"/lectivo", "/api/tesoreria/core/lectivo"})
@RequiredArgsConstructor
public class LectivoController {

    private final LectivoService lectivoService;
    private final LectivoDtoMapper lectivoDtoMapper;

    @GetMapping("/")
    public ResponseEntity<List<LectivoResponse>> findAll() {
        List<LectivoResponse> responses = lectivoService.findAll().stream()
                .map(lectivoDtoMapper::toResponse)
                .collect(Collectors.toList());
		return ResponseEntity.ok(responses);
    }

    @GetMapping("/reverse")
    public ResponseEntity<List<LectivoResponse>> findAllReverse() {
        List<LectivoResponse> responses = lectivoService.findAllReverse().stream()
                .map(lectivoDtoMapper::toResponse)
                .collect(Collectors.toList());
		return ResponseEntity.ok(responses);
    }

    @GetMapping("/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<LectivoResponse>> findAllByPersona(@PathVariable BigDecimal personaId,
                                                                   @PathVariable Integer documentoId) {
        List<LectivoResponse> responses = lectivoService.findAllByPersona(personaId, documentoId).stream()
                .map(lectivoDtoMapper::toResponse)
                .collect(Collectors.toList());
		return ResponseEntity.ok(responses);
    }

    @GetMapping("/{lectivoId}")
    public ResponseEntity<LectivoResponse> findByLectivoId(@PathVariable Integer lectivoId) {
		try {
			var domain = lectivoService.findByLectivoId(lectivoId);
			return ResponseEntity.ok(lectivoDtoMapper.toResponse(domain));
		} catch (LectivoException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage());
		}
    }

    @GetMapping("/last")
    public ResponseEntity<LectivoResponse> findLast() {
        return lectivoService.findLast()
                .map(domain -> new ResponseEntity<>(lectivoDtoMapper.toResponse(domain), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PostMapping("/")
    public ResponseEntity<LectivoResponse> add(@RequestBody LectivoRequest lectivoRequest) {
        Lectivo lectivo = lectivoDtoMapper.toDomain(lectivoRequest);
        Lectivo createdLectivo = lectivoService.createLectivo(lectivo);
        return new ResponseEntity<>(lectivoDtoMapper.toResponse(createdLectivo), HttpStatus.OK);
    }

    @DeleteMapping("/{lectivoId}")
    public ResponseEntity<Void> deleteByLectivoId(@PathVariable Integer lectivoId) {
        lectivoService.deleteByLectivoId(lectivoId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
