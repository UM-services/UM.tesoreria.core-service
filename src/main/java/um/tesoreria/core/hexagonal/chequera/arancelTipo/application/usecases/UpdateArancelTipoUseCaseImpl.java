package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.UpdateArancelTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateArancelTipoUseCaseImpl implements UpdateArancelTipoUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public Optional<ArancelTipo> updateArancelTipo(Integer id, ArancelTipo arancelTipo) {
        return repository.update(id, arancelTipo);
    }
}
