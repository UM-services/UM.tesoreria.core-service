package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.kotlin.model.ChequeraSerieReemplazo;
import um.tesoreria.core.service.ChequeraSerieReemplazoService;

@RestController
@RequestMapping("/api/tesoreria/core/chequeraSerieReemplazo")
public class ChequeraSerieReemplazoController {

    private final ChequeraSerieReemplazoService service;

    public ChequeraSerieReemplazoController(ChequeraSerieReemplazoService service) {
        this.service = service;
    }

    @GetMapping("/unique/{facultadId}/{tipoChequeraId}/{chequeraSerieId}")
    public ResponseEntity<ChequeraSerieReemplazo> findByUnique(@PathVariable Integer facultadId,
                                                               @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId) {
        return new ResponseEntity<>(service.findByUnique(facultadId, tipoChequeraId, chequeraSerieId),
                HttpStatus.OK);
    }

}
