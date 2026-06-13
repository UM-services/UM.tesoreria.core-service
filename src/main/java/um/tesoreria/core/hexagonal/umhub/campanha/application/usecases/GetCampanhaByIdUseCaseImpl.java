package um.tesoreria.core.hexagonal.umhub.campanha.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in.GetCampanhaByIdUseCase;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out.CampanhaRepository;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GetCampanhaByIdUseCaseImpl implements GetCampanhaByIdUseCase {

    private final CampanhaRepository repository;

    @Override
    public Optional<Campanha> getCampanhaById(UUID id) {
        return repository.findById(id);
    }
}
