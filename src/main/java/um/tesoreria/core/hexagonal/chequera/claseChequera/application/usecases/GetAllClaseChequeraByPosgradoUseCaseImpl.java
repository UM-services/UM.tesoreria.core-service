package um.tesoreria.core.hexagonal.chequera.claseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in.GetAllClaseChequeraByPosgradoUseCase;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out.ClaseChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllClaseChequeraByPosgradoUseCaseImpl implements GetAllClaseChequeraByPosgradoUseCase {

    private final ClaseChequeraRepository repository;

    @Override
    public List<ClaseChequera> getAllClaseChequeraByPosgrado() {
        return repository.findAllByPosgrado((byte) 1);
    }

}
