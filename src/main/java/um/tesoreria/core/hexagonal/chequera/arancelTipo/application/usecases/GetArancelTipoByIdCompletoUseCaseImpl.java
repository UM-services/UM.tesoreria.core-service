package um.tesoreria.core.hexagonal.chequera.arancelTipo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in.GetArancelTipoByIdCompletoUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.out.ArancelTipoRepository;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetArancelTipoByIdCompletoUseCaseImpl implements GetArancelTipoByIdCompletoUseCase {
    private final ArancelTipoRepository repository;
    @Override
    public Optional<ArancelTipo> getArancelTipoByIdCompleto(Integer id) {
        return repository.findByArancelTipoIdCompleto(id);
    }
}
