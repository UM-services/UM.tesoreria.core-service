package um.tesoreria.core.hexagonal.pagos.entrega.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.in.UpdateEntregaAsientoUseCase;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out.EntregaRepository;

@Component
@RequiredArgsConstructor
public class UpdateEntregaAsientoUseCaseImpl implements UpdateEntregaAsientoUseCase {

    private final EntregaRepository entregaRepository;

    @Override
    public Entrega updateAsiento(Entrega entrega) {
        return entregaRepository.save(entrega);
    }
}
