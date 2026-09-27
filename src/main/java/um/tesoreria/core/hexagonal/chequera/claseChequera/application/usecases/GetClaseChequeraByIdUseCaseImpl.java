package um.tesoreria.core.hexagonal.chequera.claseChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.model.ClaseChequera;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.in.GetClaseChequeraByIdUseCase;
import um.tesoreria.core.hexagonal.chequera.claseChequera.domain.ports.out.ClaseChequeraRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetClaseChequeraByIdUseCaseImpl implements GetClaseChequeraByIdUseCase {

    private final ClaseChequeraRepository repository;

    @Override
    public Optional<ClaseChequera> getClaseChequeraById(Integer claseChequeraId) {
        return repository.findByClaseChequeraId(claseChequeraId);
    }
}
