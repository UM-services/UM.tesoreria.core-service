package um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;

public interface CreateCampanhaUseCase {
    Campanha createCampanha(Campanha campanha);
}
