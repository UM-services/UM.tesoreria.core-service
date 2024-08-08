package um.tesoreria.core.service;

import org.springframework.stereotype.Service;
import um.tesoreria.core.kotlin.model.ChequeraCuotaReemplazo;
import um.tesoreria.core.kotlin.repository.ChequeraCuotaReemplazoRepository;

import java.util.List;

@Service
public class ChequeraCuotaReemplazoService {

    private final ChequeraCuotaReemplazoRepository repository;

    public ChequeraCuotaReemplazoService(ChequeraCuotaReemplazoRepository repository) {
        this.repository = repository;
    }

    public List<ChequeraCuotaReemplazo> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        return repository.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }

}
