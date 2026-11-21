package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.GetValorByIdUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetValorByIdUseCaseImpl implements GetValorByIdUseCase {

    private final ValorRepository valorRepository;

    @Override
    public Optional<Valor> getValorById(Integer valorId) {
        return valorRepository.findById(valorId);
    }
}
