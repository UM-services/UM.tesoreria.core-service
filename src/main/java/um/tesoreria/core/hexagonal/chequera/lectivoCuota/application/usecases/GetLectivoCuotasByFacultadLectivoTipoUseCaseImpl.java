package um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.GetLectivoCuotasByFacultadLectivoTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.out.LectivoCuotaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetLectivoCuotasByFacultadLectivoTipoUseCaseImpl implements GetLectivoCuotasByFacultadLectivoTipoUseCase {

    private final LectivoCuotaRepository lectivoCuotaRepository;

    @Override
    public List<LectivoCuota> getLectivoCuotasByFacultadLectivoTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId) {
        return lectivoCuotaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraId(facultadId, lectivoId, tipoChequeraId);
    }
}
