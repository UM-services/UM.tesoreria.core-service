package um.tesoreria.core.hexagonal.chequera.emision.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.CompletarChequerasIncompletasService;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.IncompletasJobService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroIncompletas;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobDetalle;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.ResultadoIncompleta;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.IncompletasChequerasUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.IncompletasJobRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class IncompletasChequerasFacade implements IncompletasChequerasUseCase {

    private static final List<IncompletasTareaEstado> CON_BORRADOR = List.of(
            IncompletasTareaEstado.BORRADOR_CREADO,
            IncompletasTareaEstado.BORRADOR_REUTILIZADO);

    private final IncompletasJobRepository repository;
    private final CompletarChequerasIncompletasService orquestador;
    private final IncompletasJobService jobService;
    private final UsuarioService usuarioService;
    private final TransactionOperations transaction;

    @Override
    public IncompletasJob iniciarBorradores(FiltroIncompletas filtro, Long usuarioId) {
        validar(filtro);
        repository.findActivoByEtapa(IncompletasEtapa.BORRADORES).ifPresent(activo -> {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_JOB_ACTIVO,
                    "Ya hay un job de borradores en curso (" + activo.getJobId() + "): espere o cancélelo");
        });

        List<IncompletasJobTarea> tareas = orquestador.seleccionar(filtro).stream()
                .map(tarea -> {
                    tarea.setEstado(IncompletasTareaEstado.PENDIENTE);
                    return tarea;
                })
                .toList();
        if (tareas.isEmpty()) {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_SIN_TAREAS,
                    "No hay chequeras incompletas para los filtros seleccionados");
        }

        OffsetDateTime ahora = ahora();
        IncompletasJob job = IncompletasJob.builder()
                .etapa(IncompletasEtapa.BORRADORES)
                .lectivoId(filtro.getLectivoId())
                .facultadId(filtro.getFacultadId())
                .geograficaId(filtro.getGeograficaId())
                .claseChequeraId(filtro.getClaseChequeraId())
                .soloNoEnviadas(filtro.isSoloNoEnviadas())
                .estado(IncompletasJobEstado.PENDIENTE)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .usuarioLogin(login(usuarioId))
                .build();
        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

    @Override
    public IncompletasJob iniciarEmision(Long jobOrigenId, List<Long> borradorIds, Long usuarioId) {
        IncompletasJob origen = repository.findByJobId(jobOrigenId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.INCOMPLETAS_JOB_INEXISTENTE,
                        "No existe el job de borradores " + jobOrigenId));
        if (origen.getEtapa() != IncompletasEtapa.BORRADORES) {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_SIN_BORRADORES,
                    "El job " + jobOrigenId + " no es un job de borradores");
        }
        if (origen.getEstado() == IncompletasJobEstado.PENDIENTE
                || origen.getEstado() == IncompletasJobEstado.EN_PROCESO) {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_JOB_ACTIVO,
                    "El job de borradores todavía está en curso");
        }
        repository.findActivoByEtapa(IncompletasEtapa.EMISION).ifPresent(activo -> {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_JOB_ACTIVO,
                    "Ya hay un job de emisión en curso (" + activo.getJobId() + ")");
        });

        Set<Long> seleccionados = borradorIds == null || borradorIds.isEmpty() ? null
                : borradorIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        List<IncompletasJobTarea> tareas = repository.findTareasByJobIdAndEstadoIn(jobOrigenId, CON_BORRADOR).stream()
                .filter(tarea -> tarea.getBorradorId() != null)
                .filter(tarea -> seleccionados == null || seleccionados.contains(tarea.getBorradorId()))
                .map(tarea -> IncompletasJobTarea.builder()
                        .chequeraId(tarea.getChequeraId())
                        .personaId(tarea.getPersonaId())
                        .documentoId(tarea.getDocumentoId())
                        .facultadId(tarea.getFacultadId())
                        .tipoChequeraId(tarea.getTipoChequeraId())
                        .chequeraSerieId(tarea.getChequeraSerieId())
                        .personaNombre(tarea.getPersonaNombre())
                        .borradorId(tarea.getBorradorId())
                        .estado(IncompletasTareaEstado.PENDIENTE)
                        .build())
                .toList();
        if (tareas.isEmpty()) {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_SIN_BORRADORES,
                    "El job no tiene borradores abiertos para emitir");
        }

        IncompletasJob job = IncompletasJob.builder()
                .etapa(IncompletasEtapa.EMISION)
                .jobOrigenId(jobOrigenId)
                .lectivoId(origen.getLectivoId())
                .facultadId(origen.getFacultadId())
                .geograficaId(origen.getGeograficaId())
                .claseChequeraId(origen.getClaseChequeraId())
                .soloNoEnviadas(origen.isSoloNoEnviadas())
                .estado(IncompletasJobEstado.PENDIENTE)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .usuarioLogin(login(usuarioId))
                .build();
        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

    @Override
    public IncompletasJobDetalle consultar(Long jobId) {
        IncompletasJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.INCOMPLETAS_JOB_INEXISTENTE,
                        "No existe el job " + jobId));
        return IncompletasJobDetalle.builder()
                .job(job)
                .tareas(repository.findAllTareasByJobId(jobId))
                .build();
    }

    @Override
    public IncompletasJob cancelar(Long jobId) {
        IncompletasJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.INCOMPLETAS_JOB_INEXISTENTE,
                        "No existe el job " + jobId));
        if (job.getEstado() != IncompletasJobEstado.PENDIENTE
                && job.getEstado() != IncompletasJobEstado.EN_PROCESO) {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_JOB_NO_CANCELABLE,
                    "El job está " + job.getEstado() + " y no se puede cancelar");
        }
        jobService.marcarCancelado(jobId);
        return repository.findByJobId(jobId).orElse(job);
    }

    @Override
    public List<IncompletasJob> pendientes() {
        return repository.findPendientes();
    }

    private static void validar(FiltroIncompletas filtro) {
        if (filtro == null || filtro.getLectivoId() == null || filtro.getFacultadId() == null
                || filtro.getGeograficaId() == null || filtro.getClaseChequeraId() == null) {
            throw new EmisionException(ReglasEmision.INCOMPLETAS_FILTRO_INCOMPLETO,
                    "Debe indicar lectivo, facultad, sede y clase de chequera");
        }
    }

    /** chequera_serie.usuario_id es un login con FK a usuario.login: se resuelve desde el id. */
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
