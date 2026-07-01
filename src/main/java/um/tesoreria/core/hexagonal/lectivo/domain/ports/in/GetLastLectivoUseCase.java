package um.tesoreria.core.hexagonal.lectivo.domain.ports.in;

import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import java.util.Optional;

public interface GetLastLectivoUseCase {
    Optional<Lectivo> getLastLectivo();
}
