package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.UnmarkTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class UnmarkTipoChequeraUseCaseImpl implements UnmarkTipoChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public void unmarkTipoChequera() {
        repository.unmark();
    }
}
