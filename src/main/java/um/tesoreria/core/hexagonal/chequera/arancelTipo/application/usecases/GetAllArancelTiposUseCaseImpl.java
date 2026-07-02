package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.GetAllArancelTiposUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllArancelTiposUseCaseImpl implements GetAllArancelTiposUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public List<ArancelTipo> getAllArancelTipos() {
        return repository.findAll();
    }
}
