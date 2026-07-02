package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.GetAllTipoChequeraByClaseChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllTipoChequeraByClaseChequeraUseCaseImpl implements GetAllTipoChequeraByClaseChequeraUseCase {
    private final TipoChequeraRepository repository;

    @Override
    public List<TipoChequera> getAllTipoChequeraByClaseChequera(Integer claseChequeraId) {
        return repository.findAllByClaseChequeraId(claseChequeraId);
    }

    @Override
    public List<TipoChequera> getAllTipoChequeraByClaseChequeraIds(List<Integer> claseChequeraIds) {
        return repository.findAllByClaseChequeraIds(claseChequeraIds);
    }
}
