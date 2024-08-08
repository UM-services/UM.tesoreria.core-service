package um.tesoreria.core.service;

import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraSerieReemplazoException;
import um.tesoreria.core.kotlin.model.ChequeraSerieReemplazo;
import um.tesoreria.core.kotlin.repository.ChequeraSerieReemplazoRepository;

@Service
public class ChequeraSerieReemplazoService {

    private final ChequeraSerieReemplazoRepository repository;

    public ChequeraSerieReemplazoService(ChequeraSerieReemplazoRepository repository) {
        this.repository = repository;
    }

    public ChequeraSerieReemplazo findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return repository.findByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId).orElseThrow(() -> new ChequeraSerieReemplazoException(facultadId, tipoChequeraId, chequeraSerieId));
    }

}
