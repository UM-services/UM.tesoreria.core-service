package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.persistence.entity.CampanhaEntity;

import java.util.UUID;

@Repository
public interface JpaCampanhaRepository extends JpaRepository<CampanhaEntity, UUID> {
    boolean existsByCampanhaId(UUID campanhaId);

    @Modifying
    void deleteByCampanhaId(UUID campanhaId);
}
