package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJob;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTarea;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobTareaEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.ports.out.AsientoIngresoJobRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.AsientoIngresoJobEntity;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.mapper.AsientoIngresoJobMapper;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.repository.JpaAsientoIngresoJobRepository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.repository.JpaAsientoIngresoJobTareaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaAsientoIngresoJobRepositoryAdapter implements AsientoIngresoJobRepository {

    private static final List<AsientoIngresoJobEstado> FINALIZADOS = List.of(
            AsientoIngresoJobEstado.COMPLETADO,
            AsientoIngresoJobEstado.CANCELADO,
            AsientoIngresoJobEstado.ERROR);

    private final JpaAsientoIngresoJobRepository jobRepository;
    private final JpaAsientoIngresoJobTareaRepository tareaRepository;
    private final AsientoIngresoJobMapper mapper;

    @Override
    public AsientoIngresoJob create(AsientoIngresoJob job, List<AsientoIngresoJobTarea> tareas) {
        AsientoIngresoJobEntity entity = jobRepository.saveAndFlush(mapper.toEntity(job));
        List<AsientoIngresoJobTarea> conJob = tareas.stream()
                .map(tarea -> {
                    tarea.setJobId(entity.getJobId());
                    return tarea;
                })
                .toList();
        tareaRepository.saveAll(conJob.stream().map(mapper::toEntity).toList());
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<AsientoIngresoJob> findByJobId(Long jobId) {
        return jobRepository.findById(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<AsientoIngresoJob> findWithLockByJobId(Long jobId) {
        return jobRepository.findWithLockByJobId(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<AsientoIngresoJob> claimNextPending() {
        return jobRepository.findFirstByEstadoOrderByJobIdAsc(AsientoIngresoJobEstado.PENDIENTE)
                .map(mapper::toDomain);
    }

    @Override
    public List<AsientoIngresoJobTarea> findAllTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdOrderByFechaContableAscTipoPagoIdAsc(jobId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<AsientoIngresoJobTarea> findPendingTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdAndEstadoOrderByFechaContableAscTipoPagoIdAsc(
                        jobId, AsientoIngresoJobTareaEstado.PENDIENTE).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public long countTareasByJobIdAndEstado(Long jobId, AsientoIngresoJobTareaEstado estado) {
        return tareaRepository.countByJobIdAndEstado(jobId, estado);
    }

    @Override
    public AsientoIngresoJob update(AsientoIngresoJob job) {
        return mapper.toDomain(jobRepository.save(mapper.toEntity(job)));
    }

    @Override
    public AsientoIngresoJobTarea updateTarea(AsientoIngresoJobTarea tarea) {
        return mapper.toDomain(tareaRepository.save(mapper.toEntity(tarea)));
    }

    @Override
    public int requeueStale(OffsetDateTime heartbeatLimite) {
        List<AsientoIngresoJobEntity> vencidos = jobRepository.findAllByEstadoAndHeartbeatBefore(
                AsientoIngresoJobEstado.EN_PROCESO, heartbeatLimite);
        vencidos.forEach(job -> {
            job.setEstado(AsientoIngresoJobEstado.PENDIENTE);
            job.setHeartbeat(null);
        });
        jobRepository.saveAll(vencidos);
        return vencidos.size();
    }

    @Override
    public int deleteFinalizedBefore(OffsetDateTime finalizadoLimite) {
        List<AsientoIngresoJobEntity> viejos = jobRepository.findAllByEstadoInAndFinalizadoBefore(
                FINALIZADOS, finalizadoLimite);
        jobRepository.deleteAll(viejos);
        return viejos.size();
    }

}
