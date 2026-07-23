package um.tesoreria.core.hexagonal.setup.domain.ports.in;

import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import java.util.Optional;

public interface GetLastSetupUseCase {
    Optional<Setup> getLastSetup();
}
