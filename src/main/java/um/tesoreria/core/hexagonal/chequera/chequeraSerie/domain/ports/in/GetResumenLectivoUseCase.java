package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;
import java.util.List;

public interface GetResumenLectivoUseCase {
    List<FacultadSedeChequeraDto> resumenLectivo(Integer lectivoId);
}
