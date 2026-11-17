package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.AsientoIngresoJobTareaEntity;

import java.util.List;

@Repository
public interface JpaAsientoIngresoJobTareaRepository extends JpaRepository<AsientoIngresoJobTareaEntity, Long> {

    List<AsientoIngresoJobTareaEntity> findAllByJobIdOrderByFechaContableAscTipoPagoIdAsc(Long jobId);

    List<AsientoIngresoJobTareaEntity> findAllByJobIdAndEstadoOrderByFechaContableAscTipoPagoIdAsc(
            Long jobId, AsientoIngresoJobTareaEstado estado);

    long countByJobIdAndEstado(Long jobId, AsientoIngresoJobTareaEstado estado);

}
