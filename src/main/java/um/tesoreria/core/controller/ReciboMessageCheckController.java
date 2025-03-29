package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import um.tesoreria.core.exception.ReciboMessageCheckException;
import um.tesoreria.core.model.ReciboMessageCheck;
import um.tesoreria.core.service.ReciboMessageCheckService;

import java.util.UUID;

@RestController
@RequestMapping({"/reciboMessageCheck", "/api/tesoreria/core/reciboMessageCheck"})
public class ReciboMessageCheckController {

    private final ReciboMessageCheckService service;

    public ReciboMessageCheckController(ReciboMessageCheckService service) {
        this.service = service;
    }

    @GetMapping("/{reciboMessageCheckId}")
    public ResponseEntity<ReciboMessageCheck> findByReciboMessageCheckId(@PathVariable UUID reciboMessageCheckId) {
        try {
            return ResponseEntity.ok(service.findByReciboMessageCheckId(reciboMessageCheckId));
        } catch (ReciboMessageCheckException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<ReciboMessageCheck> add(@RequestBody ReciboMessageCheck reciboMessageCheck) {
        return ResponseEntity.ok(service.add(reciboMessageCheck));
    }

}
