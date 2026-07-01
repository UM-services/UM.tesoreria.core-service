package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.MarkTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

@Component
@RequiredArgsConstructor
public class MarkTipoChequeraUseCaseImpl implements MarkTipoChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public TipoChequera markTipoChequera(Integer tipoChequeraId, Byte imprimir) {
        return repository.mark(tipoChequeraId, imprimir);
    }
}
