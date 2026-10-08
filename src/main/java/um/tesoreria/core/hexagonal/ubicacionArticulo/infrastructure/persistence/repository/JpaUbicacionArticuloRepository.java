package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.entity.UbicacionArticuloEntity;
import java.util.Optional;

@Repository
public interface JpaUbicacionArticuloRepository extends JpaRepository<UbicacionArticuloEntity, Long> {
    Optional<UbicacionArticuloEntity> findByUbicacionIdAndArticuloId(Integer ubicacionId, Long articuloId);
    java.util.List<UbicacionArticuloEntity> findAllByArticuloId(Long articuloId);

    /** Solo el id: no carga la entidad, así la lectura con bloqueo posterior es la que trae su estado. */
    @Query("select u.ubicacionArticuloId from UbicacionArticuloEntity u where u.ubicacionId = :ubicacionId and u.articuloId = :articuloId")
    Optional<Long> findIdByUbicacionIdAndArticuloId(@Param("ubicacionId") Integer ubicacionId, @Param("articuloId") Long articuloId);
}
