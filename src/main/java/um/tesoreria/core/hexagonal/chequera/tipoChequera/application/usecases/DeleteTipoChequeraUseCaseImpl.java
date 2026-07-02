package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.DeleteTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class DeleteTipoChequeraUseCaseImpl implements DeleteTipoChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public void deleteTipoChequera(Integer tipoChequeraId) {
        repository.deleteById(tipoChequeraId);
    }
}
