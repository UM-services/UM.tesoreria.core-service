package um.tesoreria.core.hexagonal.setup.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.setup.domain.model.Setup;
import um.tesoreria.core.hexagonal.setup.domain.ports.out.SetupRepository;
import um.tesoreria.core.hexagonal.setup.infrastructure.persistence.mapper.SetupMapper;
import um.tesoreria.core.hexagonal.setup.infrastructure.persistence.repository.JpaSetupRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaSetupRepositoryAdapter implements SetupRepository {

    private final JpaSetupRepository jpaSetupRepository;
    private final SetupMapper setupMapper;

    @Override
    public Optional<Setup> findLast() {
        return jpaSetupRepository.findTopByOrderBySetupIdDesc().map(setupMapper::toDomain);
    }
}
