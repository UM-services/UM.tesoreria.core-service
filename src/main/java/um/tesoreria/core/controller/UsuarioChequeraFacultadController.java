package um.tesoreria.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.model.UsuarioChequeraFacultad;
import um.tesoreria.core.service.UsuarioChequeraFacultadService;

import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/usuarioChequeraFacultad")
public class UsuarioChequeraFacultadController {

    private final UsuarioChequeraFacultadService service;

    public UsuarioChequeraFacultadController(UsuarioChequeraFacultadService service) {
        this.service = service;
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UsuarioChequeraFacultad>> findAllByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(service.findAllByUserId(userId));
    }

}
