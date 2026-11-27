package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioJobRepository;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioJobEntity;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioJobTareaEntity;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.mapper.EnvioJobMapper;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository.JpaEnvioJobRepository;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository.JpaEnvioJobTareaRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@Component
@Slf4j
@RequiredArgsConstructor
public class JpaEnvioJobRepositoryAdapter implements EnvioJobRepository {

    private static final List<EnvioJobEstado> FINALIZADOS = List.of(
            EnvioJobEstado.COMPLETADO,
            EnvioJobEstado.CANCELADO,
            EnvioJobEstado.ERROR);

    private static final List<EnvioJobEstado> ACTIVOS = List.of(
            EnvioJobEstado.PENDIENTE,
            EnvioJobEstado.EN_PROCESO);

    private static final List<EnvioTareaEstado> OK = List.of(
            EnvioTareaEstado.ENVIADA,
            EnvioTareaEstado.RETENIDA,
            EnvioTareaEstado.ENCOLADA,
            EnvioTareaEstado.CORREO_CAPTURADO,
            EnvioTareaEstado.DEBITO_EXTENDIDO);

    private static final List<EnvioTareaEstado> OMITIDAS = List.of(
            EnvioTareaEstado.OMITIDA,
            EnvioTareaEstado.SIN_CORREOS);

    private static final List<EnvioTareaEstado> ERRORES = List.of(
            EnvioTareaEstado.ERROR,
            EnvioTareaEstado.ERROR_SMTP);

    private final JpaEnvioJobRepository jobRepository;
    private final JpaEnvioJobTareaRepository tareaRepository;
    private final EnvioJobMapper mapper;

    @Override
    public EnvioJob create(EnvioJob job, List<EnvioJobTarea> tareas) {
        EnvioJobEntity entity = jobRepository.saveAndFlush(mapper.toEntity(job));
        tareas.forEach(tarea -> tarea.setJobId(entity.getJobId()));
        tareaRepository.saveAll(tareas.stream().map(mapper::toEntity).toList());
        return mapper.toDomain(entity);
    }

    @Override
    public Optional<EnvioJob> findByJobId(Long jobId) {
        return jobRepository.findById(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<EnvioJob> findWithLockByJobId(Long jobId) {
        return jobRepository.findWithLockByJobId(jobId).map(mapper::toDomain);
    }

    @Override
    public Optional<EnvioJob> claimNextPending() {
        return jobRepository.findFirstByEstadoOrderByJobIdAsc(EnvioJobEstado.PENDIENTE).map(mapper::toDomain);
    }

    @Override
    public Optional<EnvioJob> findActivo() {
        return jobRepository.findFirstByEstadoInOrderByJobIdAsc(ACTIVOS).map(mapper::toDomain);
    }

    @Override
    public List<EnvioJob> findPendientes() {
        return jobRepository.findAllByEstadoOrderByJobIdAsc(EnvioJobEstado.PENDIENTE).stream()
                .map(mapper::toDomain).toList();
    }

    @Override
    public List<EnvioJobTarea> findAllTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdOrderByTareaIdAsc(jobId).stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<EnvioJobTarea> findTareaById(Long tareaId) {
        return tareaRepository.findById(tareaId).map(mapper::toDomain);
    }

    @Override
    public List<EnvioJobTarea> findPendingTareasByJobId(Long jobId) {
        return tareaRepository.findAllByJobIdAndEstadoOrderByTareaIdAsc(jobId, EnvioTareaEstado.PENDIENTE)
                .stream().map(mapper::toDomain).toList();
    }

    @Override
    public long countTareasByJobIdAndEstado(Long jobId, EnvioTareaEstado estado) {
        return tareaRepository.countByJobIdAndEstado(jobId, estado);
    }

    @Override
    public long countTareasByJobIdAndEstadoIn(Long jobId, List<EnvioTareaEstado> estados) {
        return tareaRepository.countByJobIdAndEstadoIn(jobId, estados);
    }

    @Override
    public EnvioJob update(EnvioJob job) {
        return mapper.toDomain(jobRepository.save(mapper.toEntity(job)));
    }

    @Override
    public EnvioJobTarea updateTarea(EnvioJobTarea tarea) {
        return mapper.toDomain(tareaRepository.save(mapper.toEntity(tarea)));
    }

    @Override
    @Transactional
    public Optional<EnvioJobTarea> registrarAcuse(Long chequeraId, boolean enviado, String error) {
        Optional<EnvioJobTareaEntity> encontrada = tareaRepository
                .findFirstByChequeraIdAndEstadoOrderByTareaIdDesc(chequeraId, EnvioTareaEstado.ENCOLADA);
        if (encontrada.isEmpty()) {
            log.debug("Envío masivo: acuse para chequera {} sin tarea ENCOLADA (envío individual)", chequeraId);
            return Optional.empty();
        }
        return Optional.of(actualizarAcuse(encontrada.get(),
                enviado ? EnvioTareaEstado.ENVIADA : EnvioTareaEstado.ERROR_SMTP, enviado, error));
    }

    @Override
    @Transactional
    public Optional<EnvioJobTarea> registrarAcuse(Long jobId, Long chequeraId, EnvioTareaEstado estado,
                                                  boolean enviado, String mensaje) {
        Optional<EnvioJobTareaEntity> encontrada = tareaRepository
                .findFirstByJobIdAndChequeraIdOrderByTareaIdDesc(jobId, chequeraId);
        if (encontrada.isEmpty()) {
            log.debug("Envío masivo: acuse para job {} chequera {} sin tarea", jobId, chequeraId);
            return Optional.empty();
        }
        EnvioJobTareaEntity tarea = encontrada.get();
        if (tarea.getEstado() == EnvioTareaEstado.ENVIADA) {
            // Un acuse tardío de error no puede degradar una tarea ya enviada.
            return Optional.of(mapper.toDomain(tarea));
        }
        return Optional.of(actualizarAcuse(tarea, estado, enviado, mensaje));
    }

    private EnvioJobTarea actualizarAcuse(EnvioJobTareaEntity tarea, EnvioTareaEstado estado,
                                          boolean enviado, String mensaje) {
        tarea.setEstado(estado);
        tarea.setEnviado(enviado);
        tarea.setCodigo(estado.name());
        tarea.setMensaje(recortar(mensaje, 500));
        tarea.setFinalizado(OffsetDateTime.now(ZoneOffset.UTC));
        tareaRepository.save(tarea);
        actualizarContadores(tarea.getJobId());
        return mapper.toDomain(tarea);
    }

    private void actualizarContadores(Long jobId) {
        jobRepository.findById(jobId).ifPresent(job -> {
            job.setTareasOk((int) tareaRepository.countByJobIdAndEstadoIn(job.getJobId(), OK));
            job.setTareasOmitidas((int) tareaRepository.countByJobIdAndEstadoIn(job.getJobId(), OMITIDAS));
            job.setTareasError((int) tareaRepository.countByJobIdAndEstadoIn(job.getJobId(), ERRORES));
            job.setTareasPendientes((int) tareaRepository.countByJobIdAndEstado(
                    job.getJobId(), EnvioTareaEstado.PENDIENTE));
            jobRepository.save(job);
        });
    }

    @Override
    @Transactional
    public int reactivarTareas(Long jobId, List<EnvioTareaEstado> estados) {
        List<EnvioJobTareaEntity> tareas = tareaRepository
                .findAllByJobIdAndEstadoInOrderByTareaIdAsc(jobId, estados);
        tareas.forEach(tarea -> {
            tarea.setEstado(EnvioTareaEstado.PENDIENTE);
            tarea.setCodigo(null);
            tarea.setMensaje(null);
            tarea.setDetalle(null);
            tarea.setRetenida(null);
            tarea.setEnviado(null);
            tarea.setFinalizado(null);
        });
        tareaRepository.saveAll(tareas);
        return tareas.size();
    }

    @Override
    public int requeueStale(OffsetDateTime heartbeatLimite) {
        List<EnvioJobEntity> vencidos = jobRepository.findAllByEstadoAndHeartbeatBefore(
                EnvioJobEstado.EN_PROCESO, heartbeatLimite);
        vencidos.forEach(job -> {
            job.setEstado(EnvioJobEstado.PENDIENTE);
            job.setHeartbeat(null);
        });
        jobRepository.saveAll(vencidos);
        return vencidos.size();
    }

    @Override
    public int deleteFinalizedBefore(OffsetDateTime finalizadoLimite) {
        List<EnvioJobEntity> viejos = jobRepository.findAllByEstadoInAndFinalizadoBefore(
                FINALIZADOS, finalizadoLimite);
        jobRepository.deleteAll(viejos);
        return viejos.size();
    }

    private static String recortar(String texto, int maximo) {
        if (texto == null) {
            return null;
        }
        return texto.length() <= maximo ? texto : texto.substring(0, maximo);
    }
}
