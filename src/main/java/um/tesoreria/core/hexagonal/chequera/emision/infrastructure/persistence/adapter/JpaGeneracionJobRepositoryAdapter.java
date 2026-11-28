package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.GeneracionJobRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.GeneracionJobEntity;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.mapper.GeneracionJobMapper;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository.JpaGeneracionJobRepository;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository.JpaGeneracionJobTareaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaGeneracionJobRepositoryAdapter implements GeneracionJobRepository {

    private static final List<GeneracionJobEstado> FINALIZADOS = List.of(
            GeneracionJobEstado.COMPLETADO,
            GeneracionJobEstado.CANCELADO,
            GeneracionJobEstado.ERROR);

    private static final List<GeneracionJobEstado> ACTIVOS = List.of(
            GeneracionJobEstado.PENDIENTE,
            GeneracionJobEstado.EN_PROCESO);

    private final JpaGeneracionJobRepository jobRepository;
    private final JpaGeneracionJobTareaRepository tareaRepository;
    private final GeneracionJobMapper mapper;

    @Override
    public GeneracionJob create(GeneracionJob job, List<GeneracionJobTarea> tareas) {
        GeneracionJobEntity entity = jobRepository.saveAndFlush(mapper.toEntity(job));
        List<GeneracionJobTarea> conJob = tareas.stream()
                .peek(tarea -> tarea.setJobId(entity.getJobId()))
                .toList();
        tareaRepository.saveAll(conJob.stream().map(mapper::toEntity).toList());
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<GeneracionJob> findByJobId(Long jobId) {
        return jobRepository.findById(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<GeneracionJob> findWithLockByJobId(Long jobId) {
        return jobRepository.findWithLockByJobId(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<GeneracionJob> claimNextPending() {
        return jobRepository.findFirstByEstadoOrderByJobIdAsc(GeneracionJobEstado.PENDIENTE)
                .map(mapper::toDomain);
    }

    @Override
    public List<GeneracionJobTarea> findAllTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdOrderByTareaIdAsc(jobId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<GeneracionJobTarea> findPendingTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdAndEstadoOrderByTareaIdAsc(jobId, GeneracionTareaEstado.PENDIENTE)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<GeneracionJobTarea> findTareasByJobIdAndEstadoIn(Long jobId, List<GeneracionTareaEstado> estados) {
        return tareaRepository.findAllByJobIdAndEstadoInOrderByTareaIdAsc(jobId, estados)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countTareasByJobIdAndEstado(Long jobId, GeneracionTareaEstado estado) {
        return tareaRepository.countByJobIdAndEstado(jobId, estado);
    }

    @Override
    public long countTareasByJobIdAndEstadoIn(Long jobId, List<GeneracionTareaEstado> estados) {
        return tareaRepository.countByJobIdAndEstadoIn(jobId, estados);
    }

    @Override
    public GeneracionJob update(GeneracionJob job) {
        return mapper.toDomain(jobRepository.save(mapper.toEntity(job)));
    }

    @Override
    public GeneracionJobTarea updateTarea(GeneracionJobTarea tarea) {
        return mapper.toDomain(tareaRepository.save(mapper.toEntity(tarea)));
    }

    @Override
    public int requeueStale(OffsetDateTime heartbeatLimite) {
        List<GeneracionJobEntity> vencidos = jobRepository.findAllByEstadoAndHeartbeatBefore(
                GeneracionJobEstado.EN_PROCESO, heartbeatLimite);
        vencidos.forEach(job -> {
            job.setEstado(GeneracionJobEstado.PENDIENTE);
            job.setHeartbeat(null);
        });
        jobRepository.saveAll(vencidos);
        return vencidos.size();
    }

    @Override
    public int deleteFinalizedBefore(OffsetDateTime finalizadoLimite) {
        List<GeneracionJobEntity> viejos = jobRepository.findAllByEstadoInAndFinalizadoBefore(
                FINALIZADOS, finalizadoLimite);
        jobRepository.deleteAll(viejos);
        return viejos.size();
    }

    @Override
    public Optional<GeneracionJob> findActivoByEtapa(GeneracionEtapa etapa) {
        return jobRepository.findFirstByEtapaAndEstadoInOrderByJobIdDesc(etapa, ACTIVOS).map(mapper::toDomain);
    }

    @Override
    public List<GeneracionJob> findPendientes() {
        return jobRepository.findAllByEstadoOrderByJobIdAsc(GeneracionJobEstado.PENDIENTE)
                .stream().map(mapper::toDomain).toList();
    }
}
