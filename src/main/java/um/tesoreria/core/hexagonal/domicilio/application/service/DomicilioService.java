package um.tesoreria.core.hexagonal.domicilio.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.*;
import um.tesoreria.core.model.view.DomicilioKey;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DomicilioService {

    private final CreateDomicilioUseCase createDomicilioUseCase;
    private final UpdateDomicilioUseCase updateDomicilioUseCase;
    private final DeleteDomicilioUseCase deleteDomicilioUseCase;
    private final GetDomicilioByIdUseCase getDomicilioByIdUseCase;
    private final GetDomicilioByUniqueUseCase getDomicilioByUniqueUseCase;
    private final GetDomicilioByPersonaIdUseCase getDomicilioByPersonaIdUseCase;
    private final GetDomicilioWithPagadorUseCase getDomicilioWithPagadorUseCase;
    private final SincronizeDomicilioUseCase sincronizeDomicilioUseCase;
    private final CaptureDomicilioUseCase captureDomicilioUseCase;
    private final GetAllDomiciliosByUnifiedsUseCase getAllDomiciliosByUnifiedsUseCase;

    public Domicilio create(Domicilio domicilio) {
        return createDomicilioUseCase.createDomicilio(domicilio);
    }

    public Optional<Domicilio> update(Long id, Domicilio domicilio) {
        return updateDomicilioUseCase.updateDomicilio(id, domicilio);
    }

    public boolean delete(Long id) {
        return deleteDomicilioUseCase.deleteDomicilio(id);
    }

    public Optional<Domicilio> findById(Long id) {
        return getDomicilioByIdUseCase.getDomicilioById(id);
    }

    public Domicilio findByUnique(BigDecimal personaId, Integer documentoId) {
        return getDomicilioByUniqueUseCase.getDomicilioByUnique(personaId, documentoId);
    }

    public Optional<Domicilio> findFirstByPersonaId(BigDecimal personaId) {
        return getDomicilioByPersonaIdUseCase.getFirstByPersonaId(personaId);
    }

    public Domicilio findWithPagador(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId) {
        return getDomicilioWithPagadorUseCase.getDomicilioWithPagador(facultadId, personaId, documentoId, lectivoId);
    }

    public Domicilio sincronize(Domicilio domicilio) {
        return sincronizeDomicilioUseCase.sincronize(domicilio);
    }

    public Integer capture(BigDecimal personaId, Integer documentoId) {
        return captureDomicilioUseCase.capture(personaId, documentoId);
    }

    public List<DomicilioKey> findAllByUnifieds(List<String> unifieds) {
        return getAllDomiciliosByUnifiedsUseCase.getAllByUnifieds(unifieds);
    }
}
