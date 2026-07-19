package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface GetCuotaActualUseCase {
    Optional<ChequeraCuota> getCuotaActual(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId,
                                           Integer productoId, Integer alternativaId, OffsetDateTime fechaActual);
}
