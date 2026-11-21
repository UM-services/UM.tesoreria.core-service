package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.exception.ValorException;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.GetValorByIdUseCase;

import java.util.Optional;

@Service("pagosValorService")
@RequiredArgsConstructor
public class ValorService {

    private final GetValorByIdUseCase getValorByIdUseCase;

    public Valor getValorById(Integer valorId) {
        return getValorByIdUseCase.getValorById(valorId)
                .orElseThrow(() -> new ValorException(valorId));
    }

    public Optional<Valor> findValorById(Integer valorId) {
        return getValorByIdUseCase.getValorById(valorId);
    }
}
