package um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;

import java.util.List;

public interface GetLectivoCuotasByFacultadLectivoTipoUseCase {
    List<LectivoCuota> getLectivoCuotasByFacultadLectivoTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId);
}
