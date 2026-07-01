package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.GetLastArancelTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLastArancelTipoUseCaseImpl implements GetLastArancelTipoUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public Optional<ArancelTipo> getLastArancelTipo() {
        return repository.findLast();
    }
}
