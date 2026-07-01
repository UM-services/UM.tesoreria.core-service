package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.GetAllTipoChequeraAsignableUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllTipoChequeraAsignableUseCaseImpl implements GetAllTipoChequeraAsignableUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public List<TipoChequera> getAllTipoChequeraAsignable(Integer facultadId, Integer lectivoId, Integer geograficaId, Integer claseChequeraId) {
        return repository.findAllAsignable(facultadId, lectivoId, geograficaId, claseChequeraId);
    }
}
