package um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import java.util.Optional;
import java.util.UUID;

public interface UpdateCampanhaUseCase {
    Optional<Campanha> updateCampanha(UUID id, Campanha campanha);
}
