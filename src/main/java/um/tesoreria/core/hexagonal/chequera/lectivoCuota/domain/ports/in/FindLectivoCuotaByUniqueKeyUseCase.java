package um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.util.Optional;

public interface FindLectivoCuotaByUniqueKeyUseCase {
    Optional<LectivoCuota> findByUniqueKey(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId);
}
