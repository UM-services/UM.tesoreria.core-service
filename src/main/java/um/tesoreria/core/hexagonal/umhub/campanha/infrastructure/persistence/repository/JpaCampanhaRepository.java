package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.entity.CampanhaEntity;

import java.util.UUID;

@Repository
public interface JpaCampanhaRepository extends JpaRepository<CampanhaEntity, UUID> {
    boolean existsByCampanhaId(UUID campanhaId);

    void deleteByCampanhaId(UUID campanhaId);
}
