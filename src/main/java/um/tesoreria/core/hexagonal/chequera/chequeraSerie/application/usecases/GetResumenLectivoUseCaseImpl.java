package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetResumenLectivoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;
import um.tesoreria.core.model.internal.FacultadSedeChequeraDto;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetResumenLectivoUseCaseImpl implements GetResumenLectivoUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    public List<FacultadSedeChequeraDto> resumenLectivo(Integer lectivoId) {
        return repository.findAllFacultadSedeByLectivo(lectivoId);
    }
}
