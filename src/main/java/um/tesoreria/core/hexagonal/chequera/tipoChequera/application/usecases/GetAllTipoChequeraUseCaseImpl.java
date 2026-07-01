package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.GetAllTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllTipoChequeraUseCaseImpl implements GetAllTipoChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public List<TipoChequera> getAllTipoChequera() {
        return repository.findAll();
    }
}
