package um.tesoreria.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import um.tesoreria.core.kotlin.model.ChequeraCuotaReemplazo;
import um.tesoreria.core.service.ChequeraCuotaReemplazoService;

import java.util.List;

@RestController
@RequestMapping("/api/tesoreria/core/chequeraCuotaReemplazo")
public class ChequeraCuotaReemplazoController {

    private final ChequeraCuotaReemplazoService service;

    public ChequeraCuotaReemplazoController(ChequeraCuotaReemplazoService service) {
        this.service = service;
    }

    @GetMapping("/chequera/{facultadId}/{tipoChequeraId}/{chequeraSerieId}/{alternativaId}")
    public ResponseEntity<List<ChequeraCuotaReemplazo>> findAllByChequera(@PathVariable Integer facultadId,
                                                                          @PathVariable Integer tipoChequeraId, @PathVariable Long chequeraSerieId,
                                                                          @PathVariable Integer alternativaId) {
        return new ResponseEntity<>(
                service.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId,
                        tipoChequeraId, chequeraSerieId, alternativaId),
                HttpStatus.OK);
    }

}
