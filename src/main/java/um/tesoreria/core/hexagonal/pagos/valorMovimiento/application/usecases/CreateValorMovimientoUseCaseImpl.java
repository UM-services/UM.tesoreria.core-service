package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.CreateValorMovimientoUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorMovimientoRepository;

@Component
@RequiredArgsConstructor
public class CreateValorMovimientoUseCaseImpl implements CreateValorMovimientoUseCase {

    private final ValorMovimientoRepository valorMovimientoRepository;

    @Override
    public ValorMovimiento createValorMovimiento(ValorMovimiento valorMovimiento) {
        return valorMovimientoRepository.save(valorMovimiento);
    }
}
