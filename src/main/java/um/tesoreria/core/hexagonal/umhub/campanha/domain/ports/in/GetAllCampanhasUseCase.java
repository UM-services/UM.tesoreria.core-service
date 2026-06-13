package um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import java.util.List;

public interface GetAllCampanhasUseCase {
    List<Campanha> getAllCampanhas();
}
