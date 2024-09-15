/**
 *
 */
package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import um.tesoreria.core.exception.CursoException;
import um.tesoreria.core.kotlin.model.Curso;
import um.tesoreria.core.service.CursoService;

import java.util.List;

/**
 * @author daniel
 *
 */
@RestController
@RequestMapping({"/curso", "/api/tesoreria/core/curso"})
public class CursoController {

    private final CursoService service;

    public CursoController(CursoService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Curso>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{cursoId}")
    public ResponseEntity<Curso> findByCursoId(@PathVariable Integer cursoId) {
        try {
            return new ResponseEntity<>(service.findByCursoId(cursoId), HttpStatus.OK);
        } catch (CursoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @GetMapping("/top/{claseChequeraId}")
    public ResponseEntity<Curso> findTopByClaseChequeraId(@PathVariable Integer claseChequeraId) {
        try {
            return new ResponseEntity<>(service.findTopByClaseChequera(claseChequeraId), HttpStatus.OK);
        } catch (CursoException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
