package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ChequeraMessageCheckException;
import um.tesoreria.core.model.ChequeraMessageCheck;
import um.tesoreria.core.service.ChequeraMessageCheckService;

import java.util.UUID;

@RestController
@RequestMapping({"/chequeraMessageCheck", "/api/tesoreria/core/chequeraMessageCheck"})
public class ChequeraMessageCheckController {

    private final ChequeraMessageCheckService service;

    public ChequeraMessageCheckController(ChequeraMessageCheckService service) {
        this.service = service;
    }

    @GetMapping("/{chequeraMessageCheckId}")
    public ResponseEntity<ChequeraMessageCheck> findByChequeraMessageCheckId(@PathVariable UUID chequeraMessageCheckId) {
        try {
            return ResponseEntity.ok(service.findByChequeraMessageCheckId(chequeraMessageCheckId));
        } catch (ChequeraMessageCheckException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ChequeraMessageCheck> add(@RequestBody ChequeraMessageCheck chequeraMessageCheck) {
        return ResponseEntity.ok(service.add(chequeraMessageCheck));
    }

}
