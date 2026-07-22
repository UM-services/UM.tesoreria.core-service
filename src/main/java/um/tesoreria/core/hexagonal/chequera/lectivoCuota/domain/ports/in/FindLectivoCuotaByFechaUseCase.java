package um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface FindLectivoCuotaByFechaUseCase {
    Optional<LectivoCuota> findCuotaByFecha(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, OffsetDateTime fecha);
}
