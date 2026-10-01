package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.entity.EscrituraHistorialEntity;

import java.util.List;

public interface JpaEscrituraHistorialRepository extends JpaRepository<EscrituraHistorialEntity, Long> {

    List<EscrituraHistorialEntity> findAllByEntidadAndEntidadClaveOrderByFechaAscEscrituraHistorialIdAsc(
            String entidad, String entidadClave);
}
