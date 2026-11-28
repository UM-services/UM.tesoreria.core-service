package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.GeneracionJobTareaEntity;

import java.util.List;

@Repository
public interface JpaGeneracionJobTareaRepository extends JpaRepository<GeneracionJobTareaEntity, Long> {

    List<GeneracionJobTareaEntity> findAllByJobIdOrderByTareaIdAsc(Long jobId);

    List<GeneracionJobTareaEntity> findAllByJobIdAndEstadoOrderByTareaIdAsc(Long jobId,
                                                                           GeneracionTareaEstado estado);

    List<GeneracionJobTareaEntity> findAllByJobIdAndEstadoInOrderByTareaIdAsc(Long jobId,
                                                                              List<GeneracionTareaEstado> estados);

    long countByJobIdAndEstado(Long jobId, GeneracionTareaEstado estado);

    long countByJobIdAndEstadoIn(Long jobId, List<GeneracionTareaEstado> estados);
}
