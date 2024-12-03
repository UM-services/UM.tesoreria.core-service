package um.tesoreria.core.controller.facade;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import um.tesoreria.core.kotlin.model.Baja;
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
        return new ResponseEntity<>(service.undoBaja(facultadId, tipoChequeraId, chequeraSerieId), HttpStatus.OK);
    }

    @PostMapping("/make")
    public ResponseEntity<Boolean> makeBaja(@RequestBody Baja baja) {
        return new ResponseEntity<>(service.makeBaja(baja), HttpStatus.OK);
    }

}
