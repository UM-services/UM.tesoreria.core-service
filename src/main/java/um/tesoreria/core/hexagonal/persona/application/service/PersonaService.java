package um.tesoreria.core.hexagonal.persona.application.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import um.tesoreria.core.hexagonal.persona.domain.model.DeudaExamen;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.*;
import um.tesoreria.core.model.dto.DeudaPersonaDto;
import um.tesoreria.core.model.view.PersonaKey;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonaService {

    private final GetPersonaByUniqueUseCase getPersonaByUniqueUseCase;
    private final GetPersonaByIdUseCase getPersonaByIdUseCase;
    private final GetPersonaByUniqueIdUseCase getPersonaByUniqueIdUseCase;
    private final FindAllSantanderUseCase findAllSantanderUseCase;
    private final SavePersonaUseCase savePersonaUseCase;
    private final FindInscriptosSinChequeraUseCase findInscriptosSinChequeraUseCase;
    private final FindInscriptosSinChequeraDefaultUseCase findInscriptosSinChequeraDefaultUseCase;
    private final FindPreInscriptosSinChequeraUseCase findPreInscriptosSinChequeraUseCase;
    private final FindDeudoresByLectivoUseCase findDeudoresByLectivoUseCase;
    private final FindUnifiedsUseCase findUnifiedsUseCase;
    private final FindByStringsUseCase findByStringsUseCase;
    private final GetDeudaPersonaUseCase getDeudaPersonaUseCase;
    private final GetInscripcionFullUseCase getInscripcionFullUseCase;
    private final GetDeudaExamenUseCase getDeudaExamenUseCase;

    public Persona findByUnique(BigDecimal personaId, Integer documentoId) {
        return getPersonaByUniqueUseCase.findByUnique(personaId, documentoId);
    }

    public Persona findByPersonaId(BigDecimal personaId) {
        return getPersonaByIdUseCase.findByPersonaId(personaId);
    }

    public List<Persona> findAllSantander() {
        return findAllSantanderUseCase.findAllSantander();
    }

    public DeudaPersonaDto deudaByPersona(BigDecimal personaId, Integer documentoId) {
        return getDeudaPersonaUseCase.deudaByPersona(personaId, documentoId);
    }

    public DeudaPersonaDto deudaByPersonaExtended(BigDecimal personaId, Integer documentoId) {
        return getDeudaPersonaUseCase.deudaByPersonaExtended(personaId, documentoId);
    }

    public List<PersonaKey> findAllInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer curso) {
        return findInscriptosSinChequeraUseCase.findAllInscriptosSinChequera(facultadId, lectivoId, geograficaId, curso);
    }

    public List<PersonaKey> findAllInscriptosSinChequeraDefault(Integer facultadId, Integer lectivoId,
            Integer geograficaId, Integer claseChequeraId, Integer curso) {
        return findInscriptosSinChequeraDefaultUseCase.findAllInscriptosSinChequeraDefault(facultadId, lectivoId, geograficaId, claseChequeraId, curso);
    }

    public List<PersonaKey> findAllPreInscriptosSinChequera(Integer facultadId, Integer lectivoId, Integer geograficaId) {
        return findPreInscriptosSinChequeraUseCase.findAllPreInscriptosSinChequera(facultadId, lectivoId, geograficaId);
    }

    public List<PersonaKey> findAllDeudorByLectivoId(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer cuotas) {
        return findDeudoresByLectivoUseCase.findAllDeudorByLectivoId(facultadId, lectivoId, geograficaId, cuotas);
    }

    public List<PersonaKey> findByUnifieds(List<String> unifieds) {
        return findUnifiedsUseCase.findByUnifieds(unifieds);
    }

    public List<PersonaKey> findByStrings(List<String> conditions) {
        return findByStringsUseCase.findByStrings(conditions);
    }

    public Persona findByUniqueId(Long uniqueId) {
        return getPersonaByUniqueIdUseCase.findByUniqueId(uniqueId);
    }

    public Persona create(Persona persona) {
        return savePersonaUseCase.create(persona);
    }

    public Persona update(Persona persona, Long uniqueId) {
        return savePersonaUseCase.update(persona, uniqueId);
    }

    public InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return getInscripcionFullUseCase.findInscripcionFull(facultadId, personaId, documentoId, lectivoId);
    }

    public DeudaExamen getDeudaExamenByFacultadAndPersona(Integer facultadId, BigDecimal personaId, Integer documentoId, OffsetDateTime fechaExamen) {
        return getDeudaExamenUseCase.getDeudaExamenByFacultadAndPersona(facultadId, personaId, documentoId, fechaExamen);
    }

}
