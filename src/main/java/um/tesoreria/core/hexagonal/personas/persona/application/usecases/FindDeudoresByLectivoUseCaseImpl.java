package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.FindDeudoresByLectivoUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindDeudoresByLectivoUseCaseImpl implements FindDeudoresByLectivoUseCase {

    private final ChequeraSerieService chequeraSerieService;
    private final ChequeraCuotaService chequeraCuotaService;
    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findAllDeudorByLectivoId(Integer facultadId, Integer lectivoId, Integer geograficaId,
            Integer cuotas) {
        List<String> unifieds = new ArrayList<>();
        for (ChequeraSerie serie : chequeraSerieService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId,
                facultadId, geograficaId)) {
            if (cuotas <= chequeraCuotaService.findAllDebidas(facultadId, serie.getTipoChequeraId(),
                    serie.getChequeraSerieId(), serie.getAlternativaId(), OffsetDateTime.now()).size()) {
                unifieds.add(serie.getPersonaKey());
            }
        }
        return personaKeyService.findAllByUnifiedIn(unifieds, List.of("apellido", "nombre"));
    }
}
