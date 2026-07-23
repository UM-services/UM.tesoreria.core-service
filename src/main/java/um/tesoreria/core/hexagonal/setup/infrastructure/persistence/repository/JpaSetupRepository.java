package um.tesoreria.core.hexagonal.setup.infrastructure.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import um.tesoreria.core.hexagonal.setup.infrastructure.persistence.entity.SetupEntity;

@Repository
public interface JpaSetupRepository extends JpaRepository<SetupEntity, Integer> {

    Optional<SetupEntity> findTopByOrderBySetupIdDesc();
}
