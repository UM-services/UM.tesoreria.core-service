package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.CreateTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class CreateTipoChequeraUseCaseImpl implements CreateTipoChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public TipoChequera createTipoChequera(TipoChequera tipoChequera) {
        return repository.create(tipoChequera);
    }
}
