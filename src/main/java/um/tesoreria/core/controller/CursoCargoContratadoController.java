/**
 *
 */
package um.tesoreria.core.controller;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import um.tesoreria.core.kotlin.model.CursoCargoContratado;
import um.tesoreria.core.service.CursoCargoContratadoService;

/**
 * @author daniel
 */
@RestController
@RequestMapping({"/cursocargocontratado", "/api/core/cursocargocontratado"})
@Slf4j
@RequiredArgsConstructor
public class CursoCargoContratadoController {

    private final CursoCargoContratadoService service;

    @GetMapping("/cursos/contrato/{contratoId}/periodo/{anho}/{mes}/persona/{personaId}/{documentoId}")
    public ResponseEntity<List<CursoCargoContratado>> findAllByContrato(@PathVariable Long contratoId,
                                                                        @PathVariable Integer anho,
                                                                        @PathVariable Integer mes,
                                                                        @PathVariable BigDecimal personaId,
                                                                        @PathVariable Integer documentoId
    ) {
        return ResponseEntity.ok(service.findAllByContrato(contratoId, anho, mes, personaId, documentoId));
    }

    @GetMapping("/cursos/persona/{personaId}/{documentoId}/periodo/{anho}/{mes}")
    public ResponseEntity<List<CursoCargoContratado>> findAllByPersona(@PathVariable BigDecimal personaId,
                                                                       @PathVariable Integer documentoId,
                                                                       @PathVariable Integer anho,
                                                                       @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByPersona(personaId, documentoId, anho, mes));
    }

    @GetMapping("/curso/{cursoId}/{anho}/{mes}")
    public ResponseEntity<List<CursoCargoContratado>> findAllByCurso(@PathVariable Long cursoId,
                                                                     @PathVariable Integer anho,
                                                                     @PathVariable Integer mes) {
        return ResponseEntity.ok(service.findAllByCurso(cursoId, anho, mes));
    }

    @GetMapping("/{cursocargocontratadoId}")
    public ResponseEntity<CursoCargoContratado> findByCursoCargo(@PathVariable Long cursocargocontratadoId) {
        return ResponseEntity.ok(service.findByCursoCargo(cursocargocontratadoId));
    }

    @PostMapping("/")
    public ResponseEntity<CursoCargoContratado> add(@RequestBody CursoCargoContratado cursocargocontratado) {
        return ResponseEntity.ok(service.add(cursocargocontratado));
    }

    @PutMapping("/{cursoCargoContratadoId}")
    public ResponseEntity<CursoCargoContratado> update(@RequestBody CursoCargoContratado cursoCargoContratado,
                                                       @PathVariable Long cursoCargoContratadoId) {
        return ResponseEntity.ok(service.update(cursoCargoContratado, cursoCargoContratadoId));
    }

    @DeleteMapping("/{cursoCargoContratadoId}")
    public ResponseEntity<Void> delete(@PathVariable Long cursoCargoContratadoId) {
        service.delete(cursoCargoContratadoId);
        return ResponseEntity.noContent().build();
    }

}
