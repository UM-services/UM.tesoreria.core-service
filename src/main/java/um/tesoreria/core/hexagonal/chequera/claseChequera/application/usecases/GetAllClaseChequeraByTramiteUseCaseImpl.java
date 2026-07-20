package um.tesoreria.core.hexagonal.chequera.claseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in.GetAllClaseChequeraByTramiteUseCase;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out.ClaseChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllClaseChequeraByTramiteUseCaseImpl implements GetAllClaseChequeraByTramiteUseCase {

    private final ClaseChequeraRepository repository;

    @Override
    public List<ClaseChequera> getAllClaseChequeraByTramite() {
        return repository.findAllByTramite((byte) 1);
    }

}
