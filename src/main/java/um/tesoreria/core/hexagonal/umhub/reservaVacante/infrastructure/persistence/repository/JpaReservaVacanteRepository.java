package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.entity.ReservaVacanteEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReservaVacanteRepository extends JpaRepository<ReservaVacanteEntity, UUID> {

    Optional<ReservaVacanteEntity> findByReservaVacanteId(UUID reservaVacanteId);

}
