package um.tesoreria.core.hexagonal.umhub.campanha.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in.UpdateCampanhaUseCase;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out.CampanhaRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpdateCampanhaUseCaseImpl implements UpdateCampanhaUseCase {

    private final CampanhaRepository repository;

    @Override
    public Optional<Campanha> updateCampanha(UUID id, Campanha campanha) {
        return repository.update(id, campanha);
    }
}
