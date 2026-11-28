package um.tesoreria.core.hexagonal.chequera.envio.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.envio.application.exception.EnvioException;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioJobService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobDetalle;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioTareaEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.FiltroEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.in.EnvioMasivoUseCase;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioJobRepository;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class EnvioMasivoFacade implements EnvioMasivoUseCase {

    private static final List<EnvioTareaEstado> FALLIDAS = List.of(
            EnvioTareaEstado.ERROR,
            EnvioTareaEstado.ERROR_SMTP);

    private static final List<EnvioTareaEstado> REINTENTABLES = List.of(
            EnvioTareaEstado.ERROR,
            EnvioTareaEstado.ERROR_SMTP,
            EnvioTareaEstado.OMITIDA,
            EnvioTareaEstado.SIN_CORREOS);

    private final EnvioJobRepository repository;
    private final ChequeraEnvioAltaReader reader;
    private final EnvioJobService jobService;
    private final UsuarioService usuarioService;
    private final TransactionOperations transaction;

    @Override
    public EnvioJob iniciar(FiltroEnvio filtro, Long usuarioId) {
        validar(filtro);
        repository.findActivo().ifPresent(activo -> {
            throw new EnvioException("ENVIO_JOB_ACTIVO",
                    "Ya hay un job de envío en curso (" + activo.getJobId() + "): espere o cancélelo");
        });

        Set<Long> seleccionadas = filtro.getChequeraIds() == null || filtro.getChequeraIds().isEmpty() ? null
                : filtro.getChequeraIds().stream().filter(Objects::nonNull).collect(Collectors.toSet());

        List<EnvioJobTarea> tareas = reader
                .findAllByLectivoIdAndFacultadIdAndGeograficaId(filtro.getLectivoId(), filtro.getFacultadId(),
                        filtro.getGeograficaId())
                .stream()
                // «No Enviadas» del legacy: se aplica acá, no sólo en la grilla, para que no divergan.
                .filter(alta -> !filtro.isSoloNoEnviadas() || !alta.enviado())
                .filter(alta -> seleccionadas == null || seleccionadas.contains(alta.chequeraId()))
                .map(alta -> EnvioJobTarea.builder()
                        .chequeraId(alta.chequeraId())
                        .personaId(alta.personaId())
                        .documentoId(alta.documentoId())
                        .facultadId(alta.facultadId())
                        .tipoChequeraId(alta.tipoChequeraId())
                        .chequeraSerieId(alta.chequeraSerieId())
                        .personaNombre(alta.apellidoNombre())
                        .estado(EnvioTareaEstado.PENDIENTE)
                        .build())
                .toList();
        if (tareas.isEmpty()) {
            throw new EnvioException("ENVIO_SIN_TAREAS", "No hay chequeras para los filtros seleccionados");
        }

        EnvioJob job = EnvioJob.builder()
                .operacion(filtro.getOperacion())
                .lectivoId(filtro.getLectivoId())
                .facultadId(filtro.getFacultadId())
                .geograficaId(filtro.getGeograficaId())
                .soloNoEnviadas(filtro.isSoloNoEnviadas())
                .codigoBarras(filtro.isCodigoBarras())
                .estado(EnvioJobEstado.PENDIENTE)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .usuarioLogin(login(usuarioId))
                .build();
        log.info("Envío masivo: job {} de {} series ({} / {} / {})", filtro.getOperacion(), tareas.size(),
                filtro.getLectivoId(), filtro.getFacultadId(), filtro.getGeograficaId());
        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

    @Override
    public EnvioJobDetalle consultar(Long jobId) {
        EnvioJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EnvioException("ENVIO_JOB_INEXISTENTE", "No existe el job " + jobId));
        return EnvioJobDetalle.builder()
                .job(job)
                .tareas(repository.findAllTareasByJobId(jobId))
                .build();
    }

    @Override
    public EnvioJob cancelar(Long jobId) {
        EnvioJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EnvioException("ENVIO_JOB_INEXISTENTE", "No existe el job " + jobId));
        if (job.getEstado() != EnvioJobEstado.PENDIENTE && job.getEstado() != EnvioJobEstado.EN_PROCESO) {
            throw new EnvioException("ENVIO_JOB_FINALIZADO", "El job " + jobId + " ya está finalizado");
        }
        jobService.marcarCancelado(jobId);
        return repository.findByJobId(jobId).orElse(job);
    }

    @Override
    public EnvioJob reintentar(Long jobId, boolean soloFallidas) {
        EnvioJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EnvioException("ENVIO_JOB_INEXISTENTE", "No existe el job " + jobId));
        if (job.getEstado() == EnvioJobEstado.PENDIENTE || job.getEstado() == EnvioJobEstado.EN_PROCESO) {
            throw new EnvioException("ENVIO_JOB_ACTIVO", "El job " + jobId + " todavía está en curso");
        }
        repository.findActivo().ifPresent(activo -> {
            throw new EnvioException("ENVIO_JOB_ACTIVO",
                    "Ya hay un job de envío en curso (" + activo.getJobId() + ")");
        });

        int reactivadas = transaction.execute(status -> repository.reactivarTareas(jobId,
                soloFallidas ? FALLIDAS : REINTENTABLES));
        if (reactivadas == 0) {
            throw new EnvioException("ENVIO_SIN_REINTENTOS", soloFallidas
                    ? "El job no tiene tareas fallidas para reintentar"
                    : "El job no tiene tareas para reintentar");
        }
        return Objects.requireNonNull(transaction.execute(status -> {
            job.setEstado(EnvioJobEstado.PENDIENTE);
            job.setFinalizado(null);
            job.setMensaje(null);
            job.setHeartbeat(null);
            job.setTareasPendientes((int) repository.countTareasByJobIdAndEstado(jobId, EnvioTareaEstado.PENDIENTE));
            return repository.update(job);
        }));
    }

    @Override
    public List<EnvioJob> pendientes() {
        return repository.findPendientes();
    }

    private static void validar(FiltroEnvio filtro) {
        if (filtro.getOperacion() == null) {
            throw new EnvioException("ENVIO_OPERACION_REQUERIDA", "Falta la operación del job");
        }
        if (filtro.getLectivoId() == null || filtro.getFacultadId() == null || filtro.getGeograficaId() == null) {
            throw new EnvioException("ENVIO_FILTRO_INCOMPLETO", "Lectivo, unidad académica y sede son obligatorios");
        }
    }

    /** El catálogo guarda el login; el header transitorio sólo trae el id. */
    private String login(Long usuarioId) {
        if (usuarioId == null) {
            return null;
        }
        try {
            return usuarioService.findByUserId(usuarioId).map(Usuario::getLogin).orElse(null);
        } catch (RuntimeException e) {
            return null;
        }
    }

    private static OffsetDateTime ahora() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }
}
