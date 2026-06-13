package um.tesoreria.core.hexagonal.umhub.campanha.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in.DeleteCampanhaUseCase;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out.CampanhaRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DeleteCampanhaUseCaseImpl implements DeleteCampanhaUseCase {

    private final CampanhaRepository repository;

    @Override
    public boolean deleteCampanha(UUID campanhaId) {
        return repository.deleteByCampanhaId(campanhaId);
    }
}
