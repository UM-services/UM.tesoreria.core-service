package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.DeleteArancelTipoUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;

@Component
@RequiredArgsConstructor
public class DeleteArancelTipoUseCaseImpl implements DeleteArancelTipoUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public void deleteArancelTipo(Integer id) {
        repository.deleteById(id);
    }
}
