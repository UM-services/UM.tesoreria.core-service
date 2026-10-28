package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.exception.ChequeraSerieException;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.application.service.MateriasInscriptasService;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto.MateriasEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.mapper.MateriasInscriptasDtoMapper;

import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/materiasInscriptas")
@RequiredArgsConstructor
public class MateriasInscriptasController {

    private final MateriasInscriptasService service;
    private final MateriasInscriptasDtoMapper dtoMapper;

    @Operation(summary = "Materias de grado en las que está inscripto el alumno de una chequera, en el lectivo de la chequera. Un elemento por fuente consultada (SIUM, GUARANI); cada uno trae su estado (OK, SIN_INSCRIPCION, NO_DISPONIBLE) y las materias agrupadas por curso y período. Lista vacía si la chequera no es de grado. 400 si la chequera no existe. Endpoint aparte del estado de chequera: una fuente lenta no demora el estado.")
    @GetMapping("/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<List<MateriasEstadoResponse>> getMateriasInscriptas(@PathVariable Integer facultadId,
                                                                              @PathVariable Integer tipoChequeraId,
                                                                              @PathVariable Long chequeraSerieId) {
        try {
            return ResponseEntity.ok(dtoMapper.toResponse(
                    service.getMateriasInscriptas(facultadId, tipoChequeraId, chequeraSerieId)));
        } catch (ChequeraSerieException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
