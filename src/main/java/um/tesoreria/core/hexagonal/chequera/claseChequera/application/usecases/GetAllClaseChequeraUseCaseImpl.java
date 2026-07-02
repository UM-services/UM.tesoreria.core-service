package um.tesoreria.core.hexagonal.chequera.claseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in.GetAllClaseChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out.ClaseChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllClaseChequeraUseCaseImpl implements GetAllClaseChequeraUseCase {

    private final ClaseChequeraRepository repository;

    @Override
    public List<ClaseChequera> getAllClaseChequera() {
        return repository.findAll();
    }

}
