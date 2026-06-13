package um.tesoreria.core.hexagonal.umhub.campanha.domain.ports.in;

import java.util.UUID;

public interface DeleteCampanhaUseCase {
    boolean deleteCampanha(UUID id);
}
