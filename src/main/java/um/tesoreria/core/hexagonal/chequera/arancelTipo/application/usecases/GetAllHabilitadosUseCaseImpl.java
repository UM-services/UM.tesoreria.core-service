package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.GetAllHabilitadosUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllHabilitadosUseCaseImpl implements GetAllHabilitadosUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public List<ArancelTipo> getAllHabilitados() {
        return repository.findAllByHabilitado((byte) 1);
    }
}
