package um.tesoreria.core.hexagonal.umhub.campanha.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in.GetAllCampanhasUseCase;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out.CampanhaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllCampanhasUseCaseImpl implements GetAllCampanhasUseCase {

    private final CampanhaRepository repository;

    @Override
    public List<Campanha> getAllCampanhas() {
        return repository.findAll();
    }
}
