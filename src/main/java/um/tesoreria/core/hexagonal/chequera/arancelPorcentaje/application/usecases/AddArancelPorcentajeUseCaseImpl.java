package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in.AddArancelPorcentajeUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.out.ArancelPorcentajeRepository;

@Component
@RequiredArgsConstructor
public class AddArancelPorcentajeUseCaseImpl implements AddArancelPorcentajeUseCase {
    private final ArancelPorcentajeRepository repository;

    @Override
    public ArancelPorcentaje add(ArancelPorcentaje arancelPorcentaje) {
        return repository.add(arancelPorcentaje);
    }
}
