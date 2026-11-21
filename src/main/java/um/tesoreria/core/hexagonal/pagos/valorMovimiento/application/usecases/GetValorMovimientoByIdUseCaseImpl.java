package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.GetValorMovimientoByIdUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorMovimientoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetValorMovimientoByIdUseCaseImpl implements GetValorMovimientoByIdUseCase {

    private final ValorMovimientoRepository valorMovimientoRepository;

    @Override
    public Optional<ValorMovimiento> getValorMovimientoById(Long valorMovimientoId) {
        return valorMovimientoRepository.findById(valorMovimientoId);
    }
}
