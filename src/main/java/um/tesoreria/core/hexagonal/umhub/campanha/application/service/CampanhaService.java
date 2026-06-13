package um.tesoreria.core.hexagonal.umhub.campanha.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampanhaService {

    private final CreateCampanhaUseCase createCampanhaUseCase;
    private final GetCampanhaByIdUseCase getCampanhaByIdUseCase;
    private final GetAllCampanhasUseCase getAllCampanhasUseCase;
    private final UpdateCampanhaUseCase updateCampanhaUseCase;
    private final DeleteCampanhaUseCase deleteCampanhaUseCase;

    public Campanha createCampanha(Campanha campanha) {
        return createCampanhaUseCase.createCampanha(campanha);
    }

    public Optional<Campanha> getCampanhaById(UUID id) {
        return getCampanhaByIdUseCase.getCampanhaById(id);
    }

    public List<Campanha> getAllCampanhas() {
        return getAllCampanhasUseCase.getAllCampanhas();
    }

    public Optional<Campanha> updateCampanha(UUID id, Campanha campanha) {
        return updateCampanhaUseCase.updateCampanha(id, campanha);
    }

    public boolean deleteCampanha(UUID id) {
        return deleteCampanhaUseCase.deleteCampanha(id);
    }
}
