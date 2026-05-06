package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import java.util.Optional;

@Repository
public interface JpaUbicacionArticuloRepository extends JpaRepository<UbicacionArticuloEntity, Long> {
    Optional<UbicacionArticuloEntity> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId);
    java.util.List<UbicacionArticuloEntity> findAllByArticuloId(Long articuloId);
}
