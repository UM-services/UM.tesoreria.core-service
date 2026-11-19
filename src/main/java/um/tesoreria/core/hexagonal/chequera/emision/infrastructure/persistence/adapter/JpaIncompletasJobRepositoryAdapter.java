package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.IncompletasJobRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.IncompletasJobEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.mapper.IncompletasJobMapper;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository.JpaIncompletasJobRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository.JpaIncompletasJobTareaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaIncompletasJobRepositoryAdapter implements IncompletasJobRepository {

    private static final List<IncompletasJobEstado> FINALIZADOS = List.of(
            IncompletasJobEstado.COMPLETADO,
            IncompletasJobEstado.CANCELADO,
            IncompletasJobEstado.ERROR);

    private static final List<IncompletasJobEstado> ACTIVOS = List.of(
            IncompletasJobEstado.PENDIENTE,
            IncompletasJobEstado.EN_PROCESO);

    private final JpaIncompletasJobRepository jobRepository;
    private final JpaIncompletasJobTareaRepository tareaRepository;
    private final IncompletasJobMapper mapper;

    @Override
    public IncompletasJob create(IncompletasJob job, List<IncompletasJobTarea> tareas) {
        IncompletasJobEntity entity = jobRepository.saveAndFlush(mapper.toEntity(job));
        List<IncompletasJobTarea> conJob = tareas.stream()
                .peek(tarea -> tarea.setJobId(entity.getJobId()))
                .toList();
        tareaRepository.saveAll(conJob.stream().map(mapper::toEntity).toList());
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<IncompletasJob> findByJobId(Long jobId) {
        return jobRepository.findById(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<IncompletasJob> findWithLockByJobId(Long jobId) {
        return jobRepository.findWithLockByJobId(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<IncompletasJob> claimNextPending() {
        return jobRepository.findFirstByEstadoOrderByJobIdAsc(IncompletasJobEstado.PENDIENTE)
                .map(mapper::toDomain);
    }

    @Override
    public List<IncompletasJobTarea> findAllTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdOrderByTareaIdAsc(jobId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<IncompletasJobTarea> findPendingTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdAndEstadoOrderByTareaIdAsc(jobId, IncompletasTareaEstado.PENDIENTE)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<IncompletasJobTarea> findTareasByJobIdAndEstadoIn(Long jobId, List<IncompletasTareaEstado> estados) {
        return tareaRepository.findAllByJobIdAndEstadoInOrderByTareaIdAsc(jobId, estados)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countTareasByJobIdAndEstado(Long jobId, IncompletasTareaEstado estado) {
        return tareaRepository.countByJobIdAndEstado(jobId, estado);
    }

    @Override
    public long countTareasByJobIdAndEstadoIn(Long jobId, List<IncompletasTareaEstado> estados) {
        return tareaRepository.countByJobIdAndEstadoIn(jobId, estados);
    }

    @Override
    public long countByEstado(IncompletasJobEstado estado) {
        return jobRepository.countByEstado(estado);
    }

    @Override
    public IncompletasJob update(IncompletasJob job) {
        return mapper.toDomain(jobRepository.save(mapper.toEntity(job)));
    }

    @Override
    public IncompletasJobTarea updateTarea(IncompletasJobTarea tarea) {
        return mapper.toDomain(tareaRepository.save(mapper.toEntity(tarea)));
    }

    @Override
    public int requeueStale(OffsetDateTime heartbeatLimite) {
        List<IncompletasJobEntity> vencidos = jobRepository.findAllByEstadoAndHeartbeatBefore(
                IncompletasJobEstado.EN_PROCESO, heartbeatLimite);
        vencidos.forEach(job -> {
            job.setEstado(IncompletasJobEstado.PENDIENTE);
            job.setHeartbeat(null);
        });
        jobRepository.saveAll(vencidos);
        return vencidos.size();
    }

    @Override
    public int deleteFinalizedBefore(OffsetDateTime finalizadoLimite) {
        List<IncompletasJobEntity> viejos = jobRepository.findAllByEstadoInAndFinalizadoBefore(
                FINALIZADOS, finalizadoLimite);
        jobRepository.deleteAll(viejos);
        return viejos.size();
    }

    @Override
    public Optional<IncompletasJob> findActivoByEtapa(IncompletasEtapa etapa) {
        return jobRepository.findFirstByEtapaAndEstadoInOrderByJobIdDesc(etapa, ACTIVOS).map(mapper::toDomain);
    }

    @Override
    public List<IncompletasJob> findPendientes() {
        return jobRepository.findAllByEstadoOrderByJobIdAsc(IncompletasJobEstado.PENDIENTE)
                .stream().map(mapper::toDomain).toList();
    }
}
