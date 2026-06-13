package um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import java.util.Optional;
import java.util.UUID;

public interface GetCampanhaByIdUseCase {
    Optional<Campanha> getCampanhaById(UUID id);
}
