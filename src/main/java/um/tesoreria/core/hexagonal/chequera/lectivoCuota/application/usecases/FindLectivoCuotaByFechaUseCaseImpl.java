package um.tesoreria.core.hexagonal.chequera.lectivoCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.model.LectivoCuota;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.in.FindLectivoCuotaByFechaUseCase;
import um.tesoreria.core.hexagonal.chequera.lectivoCuota.domain.ports.out.LectivoCuotaRepository;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindLectivoCuotaByFechaUseCaseImpl implements FindLectivoCuotaByFechaUseCase {

    private final LectivoCuotaRepository lectivoCuotaRepository;

    @Override
    public Optional<LectivoCuota> findCuotaByFecha(Integer facultadId, Integer lectivoId, Integer tipoChequeraId, Integer productoId, Integer alternativaId, OffsetDateTime fecha) {
        var cuotas = lectivoCuotaRepository.findByFacultadIdAndLectivoIdAndTipoChequeraIdAndProductoIdAndAlternativaId(facultadId, lectivoId, tipoChequeraId, productoId, alternativaId, fecha);
        return cuotas.stream().findFirst();
    }
}
