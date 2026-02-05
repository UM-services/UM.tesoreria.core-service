package um.tesoreria.core.service.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.core.exception.ChequeraCuotaException;
import um.tesoreria.core.kotlin.model.ChequeraCuota;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.kotlin.repository.view.ChequeraSerieAltaFullRepository;
import um.tesoreria.core.service.ChequeraCuotaService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChequeraSerieAltaFullService {

    private final ChequeraSerieAltaFullRepository repository;

    @Autowired
    public ChequeraSerieAltaFullService(ChequeraSerieAltaFullRepository repository) {
        this.repository = repository;
    }

    public List<ChequeraSerieAltaFull> findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(Integer lectivoId, Integer facultadId, Integer geograficaId, Integer tipoChequeraId, OffsetDateTime fechaDesdePrimerVencimiento, ChequeraCuotaService chequeraCuotaService) {
        List<ChequeraSerieAltaFull> chequeras = new ArrayList<>();
        for (ChequeraSerieAltaFull chequeraSerieAltaFull : repository.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(lectivoId, facultadId, geograficaId, tipoChequeraId)) {
            log.debug(chequeraSerieAltaFull.getPersonaEntity().getApellidoNombre());
            try {
                ChequeraCuota chequeraCuota = chequeraCuotaService.findOneActivaImpagaPrevia(chequeraSerieAltaFull.getFacultadId(), chequeraSerieAltaFull.getTipoChequeraId(), chequeraSerieAltaFull.getChequeraSerieId(), chequeraSerieAltaFull.getAlternativaId(), fechaDesdePrimerVencimiento);
                if (chequeraCuota.getVencimiento1().isBefore(fechaDesdePrimerVencimiento)) {
                    log.debug("Agregada");
                    chequeras.add(chequeraSerieAltaFull);
                }
            } catch (ChequeraCuotaException e) {
                log.debug("Sin cuota");
            }
        }
        return chequeras;
    }
}
