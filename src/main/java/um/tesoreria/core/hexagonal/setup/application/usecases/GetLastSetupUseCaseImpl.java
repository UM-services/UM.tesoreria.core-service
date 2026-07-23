package um.tesoreria.core.hexagonal.setup.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.hexagonal.setup.domain.ports.in.GetLastSetupUseCase;
import um.tesoreria.core.hexagonal.setup.domain.ports.out.SetupRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetLastSetupUseCaseImpl implements GetLastSetupUseCase {
    private final SetupRepository repository;

    @Override
    public Optional<Setup> getLastSetup() {
        return repository.findLast();
    }
}
