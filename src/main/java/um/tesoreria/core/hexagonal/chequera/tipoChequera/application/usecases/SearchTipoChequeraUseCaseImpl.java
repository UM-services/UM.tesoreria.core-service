package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequeraSearch;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in.SearchTipoChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.out.TipoChequeraRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SearchTipoChequeraUseCaseImpl implements SearchTipoChequeraUseCase {

    private final TipoChequeraRepository repository;

    @Override
    public List<TipoChequeraSearch> searchTipoChequeras(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }

    @Override
    public List<TipoChequeraSearch> searchTipoChequerasByGeograficaId(List<String> conditions, Integer geograficaId) {
        return repository.findAllByStringsAndGeograficaId(conditions, geograficaId);
    }
}
