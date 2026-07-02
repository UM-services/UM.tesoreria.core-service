package um.tesoreria.core.service.view;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.exception.ChequeraCuotaException;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.persistence.entity.ChequeraCuotaEntity;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;
import um.tesoreria.core.kotlin.repository.view.ChequeraSerieAltaFullRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            log.debug(Objects.requireNonNull(chequeraSerieAltaFull.getPersona()).getApellidoNombre());
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
