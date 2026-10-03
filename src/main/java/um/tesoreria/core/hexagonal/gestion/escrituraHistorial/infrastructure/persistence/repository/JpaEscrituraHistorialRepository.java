package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.repository;

import org.springframework.data.repository.Repository;
import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.infrastructure.persistence.entity.EscrituraHistorialEntity;

import java.util.List;

/**
 * Solo alta y lectura: el historial no se actualiza ni se borra.
 */
public interface JpaEscrituraHistorialRepository extends Repository<EscrituraHistorialEntity, Long> {

    EscrituraHistorialEntity save(EscrituraHistorialEntity entity);

    List<EscrituraHistorialEntity> findAllByEntidadAndEntidadClaveOrderByEscrituraHistorialIdAsc(
            String entidad, String entidadClave);
}
