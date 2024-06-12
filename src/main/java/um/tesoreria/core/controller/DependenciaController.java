/**
 *
 */
package um.tesoreria.core.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import um.tesoreria.core.kotlin.model.Dependencia;
import um.tesoreria.core.service.DependenciaService;

/**
 * @author daniel
 */
@RestController
@RequestMapping("/dependencia")
public class DependenciaController {

    public final DependenciaService service;

    @Autowired
    public DependenciaController(DependenciaService service) {
        this.service = service;
    }

    @GetMapping("/")
    public ResponseEntity<List<Dependencia>> findAll() {
        return new ResponseEntity<>(service.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{dependenciaId}")
    public ResponseEntity<Dependencia> findByDependenciaId(@PathVariable Integer dependenciaId) {
        return new ResponseEntity<>(service.findByDependenciaId(dependenciaId), HttpStatus.OK);
    }

    @PutMapping("/{dependenciaId}")
    public ResponseEntity<Dependencia> update(@PathVariable Integer dependenciaId, @RequestBody Dependencia dependencia) {
        return new ResponseEntity<>(service.update(dependenciaId, dependencia), HttpStatus.OK);
    }

}
