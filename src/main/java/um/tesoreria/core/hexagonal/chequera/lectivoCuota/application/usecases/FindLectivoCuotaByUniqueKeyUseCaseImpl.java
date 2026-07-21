package um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.FindLectivoCuotaByUniqueKeyUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.out.LectivoCuotaRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindLectivoCuotaByUniqueKeyUseCaseImpl implements FindLectivoCuotaByUniqueKeyUseCase {

    private final LectivoCuotaRepository lectivoCuotaRepository;

    @Override
    public Optional<LectivoCuota> findByUniqueKey(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        return lectivoCuotaRepository.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaIdAndCuotaId(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, cuotaId);
    }
}
