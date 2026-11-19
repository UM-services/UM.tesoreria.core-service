package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.IncompletasJobTareaEntity;

import java.util.List;

@Repository
public interface JpaIncompletasJobTareaRepository extends JpaRepository<IncompletasJobTareaEntity, Long> {

    List<IncompletasJobTareaEntity> findAllByJobIdOrderByTareaIdAsc(Long jobId);

    List<IncompletasJobTareaEntity> findAllByJobIdAndEstadoOrderByTareaIdAsc(Long jobId,
                                                                           IncompletasTareaEstado estado);

    List<IncompletasJobTareaEntity> findAllByJobIdAndEstadoInOrderByTareaIdAsc(Long jobId,
                                                                              List<IncompletasTareaEstado> estados);

    long countByJobIdAndEstado(Long jobId, IncompletasTareaEstado estado);

    long countByJobIdAndEstadoIn(Long jobId, List<IncompletasTareaEstado> estados);
}
