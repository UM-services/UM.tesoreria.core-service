package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioJobTareaEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaEnvioJobTareaRepository extends JpaRepository<EnvioJobTareaEntity, Long> {

    List<EnvioJobTareaEntity> findAllByJobIdOrderByTareaIdAsc(Long jobId);

    List<EnvioJobTareaEntity> findAllByJobIdAndEstadoOrderByTareaIdAsc(Long jobId, EnvioTareaEstado estado);

    List<EnvioJobTareaEntity> findAllByJobIdAndEstadoInOrderByTareaIdAsc(Long jobId, List<EnvioTareaEstado> estados);

    long countByJobIdAndEstado(Long jobId, EnvioTareaEstado estado);

    long countByJobIdAndEstadoIn(Long jobId, List<EnvioTareaEstado> estados);

    /** Acuse del sender: la tarea ENCOLADA más reciente de esa chequera. */
    Optional<EnvioJobTareaEntity> findFirstByChequeraIdAndEstadoOrderByTareaIdDesc(Long chequeraId,
                                                                                  EnvioTareaEstado estado);

    /** Acuse de una solicitud: la tarea de ese job para esa chequera. */
    Optional<EnvioJobTareaEntity> findFirstByJobIdAndChequeraIdOrderByTareaIdDesc(Long jobId, Long chequeraId);
}
