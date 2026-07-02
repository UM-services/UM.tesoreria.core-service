package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.model.internal.CuotaPeriodoDto;
import java.util.List;

public interface FindAllPeriodosLectivoUseCase {
    List<CuotaPeriodoDto> findAllPeriodosLectivo(Integer lectivoId);
}
