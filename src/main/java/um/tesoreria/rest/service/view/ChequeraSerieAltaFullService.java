package um.tesoreria.rest.service.view;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.rest.exception.ChequeraCuotaException;
import um.tesoreria.rest.kotlin.model.ChequeraCuota;
import um.tesoreria.rest.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.rest.kotlin.repository.view.ChequeraSerieAltaFullRepository;
import um.tesoreria.rest.service.ChequeraCuotaService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ChequeraSerieAltaFullService {

    @Autowired
    private ChequeraSerieAltaFullRepository repository;

    @Autowired
    private ChequeraCuotaService chequeraCuotaService;

    public List<ChequeraSerieAltaFull> findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(Integer lectivoId, Integer facultadId, Integer geograficaId, Integer tipoChequeraId, OffsetDateTime fechaDesdePrimerVencimiento) {
        List<ChequeraSerieAltaFull> chequeras = new ArrayList<>();
        for (ChequeraSerieAltaFull chequeraSerieAltaFull : repository.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(lectivoId, facultadId, geograficaId, tipoChequeraId)) {
            log.debug(chequeraSerieAltaFull.getPersona().getApellidoNombre());
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
