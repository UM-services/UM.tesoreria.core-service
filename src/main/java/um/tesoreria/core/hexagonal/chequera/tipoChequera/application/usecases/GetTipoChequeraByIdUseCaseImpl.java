package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.GetTipoChequeraByIdUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetTipoChequeraByIdUseCaseImpl implements GetTipoChequeraByIdUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public Optional<TipoChequera> getTipoChequeraById(Integer tipoChequeraId) {
        return repository.findById(tipoChequeraId);
    }
}
