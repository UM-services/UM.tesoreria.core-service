package um.tesoreria.core.hexagonal.umhub.campanha.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in.CreateCampanhaUseCase;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out.CampanhaRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CreateCampanhaUseCaseImpl implements CreateCampanhaUseCase {

    private final CampanhaRepository repository;

    @Override
    public Campanha createCampanha(Campanha campanha) {
        if (campanha.getCampanhaId() == null) {
            campanha.setCampanhaId(UUID.randomUUID());
        }
        return repository.create(campanha);
    }
}
