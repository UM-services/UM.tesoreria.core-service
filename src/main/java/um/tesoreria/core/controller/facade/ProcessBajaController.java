package um.tesoreria.core.controller.facade;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.hexagonal.chequera.baja.infrastructure.persistence.entity.BajaEntity;
import um.tesoreria.core.service.facade.ProcessBajaService;

@RestController
@RequestMapping({"/processBaja", "/api/tesoreria/core/processBaja"})
public class ProcessBajaController {

    private final ProcessBajaService service;

    public ProcessBajaController(ProcessBajaService service) {
        this.service = service;
    }

    @GetMapping("/undo/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<Boolean> undoBaja(@PathVariable Integer facultadId, @PathVariable Integer tipoChequeraId,
                                                  @PathVariable Long chequeraSerieId) {
        return ResponseEntity.ok(service.undoBaja(facultadId, tipoChequeraId, chequeraSerieId));
    }

    @PostMapping("/make")
    public ResponseEntity<Boolean> makeBaja(@RequestBody BajaEntity baja) {
        return ResponseEntity.ok(service.makeBaja(baja));
    }

}
