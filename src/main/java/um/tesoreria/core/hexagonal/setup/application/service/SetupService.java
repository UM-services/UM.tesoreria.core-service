package um.tesoreria.core.hexagonal.setup.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.setup.application.exception.SetupException;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.hexagonal.setup.domain.ports.in.GetLastSetupUseCase;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SetupService {

    private final GetLastSetupUseCase getLastSetupUseCase;

    public Optional<Setup> findLast() {
        return getLastSetupUseCase.getLastSetup();
    }

    public Setup findLastOrFail() {
        return findLast().orElseThrow(SetupException::new);
    }
}
