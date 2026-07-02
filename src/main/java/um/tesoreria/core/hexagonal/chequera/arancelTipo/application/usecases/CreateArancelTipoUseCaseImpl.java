package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.CreateArancelTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;

@Component
@RequiredArgsConstructor
public class CreateArancelTipoUseCaseImpl implements CreateArancelTipoUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public ArancelTipo createArancelTipo(ArancelTipo arancelTipo) {
        return repository.create(arancelTipo);
    }
}
