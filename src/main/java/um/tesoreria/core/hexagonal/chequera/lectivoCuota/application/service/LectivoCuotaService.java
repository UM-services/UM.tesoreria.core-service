package um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.exception.LectivoCuotaException;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.FindLectivoCuotaByFechaUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.FindLectivoCuotaByUniqueKeyUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.GetLectivoCuotasByFacultadLectivoTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.GetLectivoCuotasByTipoUseCase;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectivoCuotaService {

    private final GetLectivoCuotasByTipoUseCase getLectivoCuotasByTipoUseCase;
    private final GetLectivoCuotasByFacultadLectivoTipoUseCase getLectivoCuotasByFacultadLectivoTipoUseCase;
    private final FindLectivoCuotaByUniqueKeyUseCase findLectivoCuotaByUniqueKeyUseCase;
    private final FindLectivoCuotaByFechaUseCase findLectivoCuotaByFechaUseCase;

    public List<LectivoCuota> findAllByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer alternativaId) {
        return getLectivoCuotasByTipoUseCase.getLectivoCuotasByTipo(facultadId, lectivoId, tipoChequeraId, alternativaId);
    }

    public List<LectivoCuota> findAllByFacultadLectivoTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return getLectivoCuotasByFacultadLectivoTipoUseCase.getLectivoCuotasByFacultadLectivoTipo(facultadId, lectivoId, tipoChequeraId);
    }

    public LectivoCuota findByUniqueKey(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        return findLectivoCuotaByUniqueKeyUseCase.findByUniqueKey(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, cuotaId)
                .orElseThrow(() -> new LectivoCuotaException(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, cuotaId));
    }

    public LectivoCuota findCuotaByFecha(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, OffsetDateTime fecha) {
        return findLectivoCuotaByFechaUseCase.findCuotaByFecha(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, fecha)
                .orElseThrow(() -> new LectivoCuotaException(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, fecha.toString()));
    }
}
