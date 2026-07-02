package um.tesoreria.core.hexagonal.lectivo.domain.ports.in;

import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface GetLectivoByFechaUseCase {
    Optional<Lectivo> getLectivoByFecha(OffsetDateTime fecha);
}
