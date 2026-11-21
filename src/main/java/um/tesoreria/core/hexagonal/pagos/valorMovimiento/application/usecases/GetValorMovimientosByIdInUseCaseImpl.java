package um.tesoreria.core.hexagonal.pagos.valorMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in.GetValorMovimientosByIdInUseCase;
import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out.ValorMovimientoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetValorMovimientosByIdInUseCaseImpl implements GetValorMovimientosByIdInUseCase {

    private final ValorMovimientoRepository valorMovimientoRepository;

    @Override
    public List<ValorMovimiento> getValorMovimientos(List<Long> valorMovimientoIds) {
        if (valorMovimientoIds == null || valorMovimientoIds.isEmpty()) {
            return List.of();
        }
        return valorMovimientoRepository.findAllByIdIn(valorMovimientoIds);
    }
}
