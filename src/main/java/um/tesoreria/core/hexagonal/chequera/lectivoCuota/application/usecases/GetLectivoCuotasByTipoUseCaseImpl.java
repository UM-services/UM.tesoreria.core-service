package um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.GetLectivoCuotasByTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.out.LectivoCuotaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetLectivoCuotasByTipoUseCaseImpl implements GetLectivoCuotasByTipoUseCase {

    private final LectivoCuotaRepository lectivoCuotaRepository;

    @Override
    public List<LectivoCuota> getLectivoCuotasByTipo(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer alternativaId) {
        return lectivoCuotaRepository.findAllByFacultadIdAndLectivoIdAndTipoChequeraIdAndAlternativaId(facultadId, lectivoId, tipoChequeraId, alternativaId);
    }
}
