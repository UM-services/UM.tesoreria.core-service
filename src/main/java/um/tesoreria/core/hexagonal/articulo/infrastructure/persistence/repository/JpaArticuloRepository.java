package um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.articulo.infrastructure.persistence.entity.ArticuloEntity;
import java.util.Optional;

@Repository
public interface JpaArticuloRepository extends JpaRepository<ArticuloEntity, Long> {
    Optional<ArticuloEntity> findTopByOrderByArticuloIdDesc();
}
