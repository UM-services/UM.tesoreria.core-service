package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.UpdateTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class UpdateTipoChequeraUseCaseImpl implements UpdateTipoChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public TipoChequera updateTipoChequera(TipoChequera tipoChequera, Integer tipoChequeraId) {
        return repository.update(tipoChequera);
    }
}
