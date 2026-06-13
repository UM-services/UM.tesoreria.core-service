package um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.out;

import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CampanhaRepository {
    Campanha create(Campanha campanha);
    Optional<Campanha> findById(UUID campanhaId);
    List<Campanha> findAll();
    Optional<Campanha> update(UUID campanhaId, Campanha campanha);
    boolean deleteByCampanhaId(UUID campanhaId);
}
