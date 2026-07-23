package um.tesoreria.core.hexagonal.setup.domain.ports.out;

import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import java.util.Optional;

public interface SetupRepository {
    Optional<Setup> findLast();
}
