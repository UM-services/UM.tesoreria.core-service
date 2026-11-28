package um.tesoreria.core.hexagonal.chequera.emision.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.emision.application.exception.EmisionException;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.BorradorEmisionService;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.GeneracionBorradoresService;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.GeneracionJobService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.BorradorChequera;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.EstadoBorrador;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroGeneracion;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobDetalle;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionPadronItem;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.PaginaBorradores;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.in.GeneracionBorradoresUseCase;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.GeneracionJobRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.service.ReglasEmision;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GeneracionBorradoresFacade implements GeneracionBorradoresUseCase {

    private static final List<GeneracionTareaEstado> CON_BORRADOR = List.of(
            GeneracionTareaEstado.BORRADOR_CREADO,
            GeneracionTareaEstado.BORRADOR_REUTILIZADO);

    private final GeneracionJobRepository repository;
    private final GeneracionBorradoresService orquestador;
    private final GeneracionJobService jobService;
    private final BorradorChequeraRepository borradorRepository;
    private final BorradorEmisionService borradorEmisionService;
    private final PersonaKeyService personaKeyService;
    private final TipoChequeraService tipoChequeraService;
    private final UsuarioService usuarioService;
    private final TransactionOperations transaction;

    @Override
    public List<GeneracionPadronItem> preview(FiltroGeneracion filtro) {
        validarPadron(filtro);
        return orquestador.preview(filtro);
    }

    @Override
    public GeneracionJob iniciarBorradores(FiltroGeneracion filtro, Long usuarioId) {
        validarPadron(filtro);
        repository.findActivoByEtapa(GeneracionEtapa.BORRADORES).ifPresent(activo -> {
            throw new EmisionException(ReglasEmision.GENERACION_JOB_ACTIVO,
                    "Ya hay un job de borradores en curso (" + activo.getJobId() + "): espere o cancélelo");
        });

        List<GeneracionJobTarea> tareas = orquestador.seleccionar(filtro).stream()
                .map(tarea -> {
                    tarea.setEstado(GeneracionTareaEstado.PENDIENTE);
                    return tarea;
                })
                .toList();
        if (tareas.isEmpty()) {
            throw new EmisionException(ReglasEmision.GENERACION_SIN_TAREAS,
                    "No hay chequeras para los filtros seleccionados");
        }

        GeneracionJob job = GeneracionJob.builder()
                .etapa(GeneracionEtapa.BORRADORES)
                .lectivoId(filtro.getLectivoId())
                .facultadId(filtro.getFacultadId())
                .geograficaId(filtro.getGeograficaId())
                .claseChequeraId(filtro.getClaseChequeraId())
                .tipoChequeraId(filtro.getTipoChequeraId())
                .soloNoEnviadas(filtro.isSoloNoEnviadas())
                .forzarDeuda(false)
                .estado(GeneracionJobEstado.PENDIENTE)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .usuarioLogin(login(usuarioId))
                .build();
        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

    @Override
    public GeneracionJob iniciarEmision(Long jobOrigenId, List<Long> borradorIds, boolean forzarDeuda,
                                        String motivoOverride, Long usuarioId) {
        GeneracionJob origen = repository.findByJobId(jobOrigenId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.GENERACION_JOB_INEXISTENTE,
                        "No existe el job de borradores " + jobOrigenId));
        if (origen.getEtapa() != GeneracionEtapa.BORRADORES) {
            throw new EmisionException(ReglasEmision.GENERACION_SIN_BORRADORES,
                    "El job " + jobOrigenId + " no es un job de borradores");
        }
        if (origen.getEstado() == GeneracionJobEstado.PENDIENTE
                || origen.getEstado() == GeneracionJobEstado.EN_PROCESO) {
            throw new EmisionException(ReglasEmision.GENERACION_JOB_ACTIVO,
                    "El job de borradores todavía está en curso");
        }
        repository.findActivoByEtapa(GeneracionEtapa.EMISION).ifPresent(activo -> {
            throw new EmisionException(ReglasEmision.GENERACION_JOB_ACTIVO,
                    "Ya hay un job de emisión en curso (" + activo.getJobId() + ")");
        });
        validarOverride(forzarDeuda, motivoOverride);
        validarPermisoForzar(forzarDeuda, usuarioId);

        Set<Long> seleccionados = borradorIds == null || borradorIds.isEmpty() ? null
                : borradorIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        List<GeneracionJobTarea> tareas = repository.findTareasByJobIdAndEstadoIn(jobOrigenId, CON_BORRADOR).stream()
                .filter(tarea -> tarea.getBorradorId() != null)
                .filter(tarea -> seleccionados == null || seleccionados.contains(tarea.getBorradorId()))
                .map(tarea -> GeneracionJobTarea.builder()
                        .chequeraId(tarea.getChequeraId())
                        .personaId(tarea.getPersonaId())
                        .documentoId(tarea.getDocumentoId())
                        .facultadId(tarea.getFacultadId())
                        .tipoChequeraId(tarea.getTipoChequeraId())
                        .chequeraSerieId(tarea.getChequeraSerieId())
                        .cursoId(tarea.getCursoId())
                        .personaNombre(tarea.getPersonaNombre())
                        .borradorId(tarea.getBorradorId())
                        .estado(GeneracionTareaEstado.PENDIENTE)
                        .build())
                .toList();
        if (tareas.isEmpty()) {
            throw new EmisionException(ReglasEmision.GENERACION_SIN_BORRADORES,
                    "El job no tiene borradores abiertos para emitir");
        }

        GeneracionJob job = GeneracionJob.builder()
                .etapa(GeneracionEtapa.EMISION)
                .jobOrigenId(jobOrigenId)
                .lectivoId(origen.getLectivoId())
                .facultadId(origen.getFacultadId())
                .geograficaId(origen.getGeograficaId())
                .claseChequeraId(origen.getClaseChequeraId())
                .tipoChequeraId(origen.getTipoChequeraId())
                .soloNoEnviadas(origen.isSoloNoEnviadas())
                .forzarDeuda(forzarDeuda)
                .motivoOverride(recortar(motivoOverride, 255))
                .estado(GeneracionJobEstado.PENDIENTE)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .usuarioLogin(login(usuarioId))
                .build();
        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

    @Override
    public GeneracionJob iniciarEmisionDirecta(FiltroGeneracion filtro, Long usuarioId) {
        validarPadron(filtro);
        validarOverride(filtro.isForzarDeuda(), filtro.getMotivoOverride());
        validarPermisoForzar(filtro.isForzarDeuda(), usuarioId);
        repository.findActivoByEtapa(GeneracionEtapa.EMISION).ifPresent(activo -> {
            throw new EmisionException(ReglasEmision.GENERACION_JOB_ACTIVO,
                    "Ya hay un job de emisión en curso (" + activo.getJobId() + ")");
        });

        Set<Long> seleccionados = filtro.getBorradorIds() == null || filtro.getBorradorIds().isEmpty() ? null
                : filtro.getBorradorIds().stream().filter(Objects::nonNull).collect(Collectors.toSet());
        Map<Integer, Integer> sedePorTipo = sedePorTipo();
        PaginaBorradores pagina = borradorRepository.findAllByLectivoIdAndFacultadIdAndTipoChequeraIdInAndEstado(
                filtro.getLectivoId(), filtro.getFacultadId(), tiposDeSede(sedePorTipo, filtro.getGeograficaId()),
                EstadoBorrador.ABIERTO, 0, 10_000);
        List<BorradorChequera> borradores = pagina.items().stream()
                .filter(borrador -> seleccionados == null || seleccionados.contains(borrador.getBorradorId()))
                .toList();
        if (borradores.isEmpty()) {
            throw new EmisionException(ReglasEmision.GENERACION_SIN_BORRADORES,
                    "No hay borradores abiertos para los filtros seleccionados");
        }

        Map<String, PersonaKey> personas = personas(borradores);
        List<GeneracionJobTarea> tareas = borradores.stream().map(borrador -> {
            PersonaKey persona = personas.get(clavePersona(borrador.getPersonaId(), borrador.getDocumentoId()));
            String nombre = persona == null ? null : (persona.getApellido() + ", " + persona.getNombre()).trim();
            return GeneracionJobTarea.builder()
                    .chequeraId(borrador.getChequeraId())
                    .personaId(borrador.getPersonaId())
                    .documentoId(borrador.getDocumentoId())
                    .facultadId(borrador.getFacultadId())
                    .tipoChequeraId(borrador.getTipoChequeraId())
                    .chequeraSerieId(borrador.getChequeraSerieId())
                    .cursoId(borrador.getCursoId())
                    .personaNombre(recortar(nombre, 120))
                    .borradorId(borrador.getBorradorId())
                    .estado(GeneracionTareaEstado.PENDIENTE)
                    .build();
        }).toList();

        GeneracionJob job = GeneracionJob.builder()
                .etapa(GeneracionEtapa.EMISION)
                .lectivoId(filtro.getLectivoId())
                .facultadId(filtro.getFacultadId())
                .geograficaId(filtro.getGeograficaId())
                .forzarDeuda(filtro.isForzarDeuda())
                .motivoOverride(recortar(filtro.getMotivoOverride(), 255))
                .estado(GeneracionJobEstado.PENDIENTE)
                .totalTareas(tareas.size())
                .tareasPendientes(tareas.size())
                .usuarioId(usuarioId)
                .usuarioLogin(login(usuarioId))
                .build();
        return Objects.requireNonNull(transaction.execute(status -> repository.create(job, tareas)));
    }

    @Override
    public GeneracionJobDetalle consultar(Long jobId) {
        GeneracionJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.GENERACION_JOB_INEXISTENTE,
                        "No existe el job " + jobId));
        return GeneracionJobDetalle.builder()
                .job(job)
                .tareas(repository.findAllTareasByJobId(jobId))
                .build();
    }

    @Override
    public GeneracionJob cancelar(Long jobId) {
        GeneracionJob job = repository.findByJobId(jobId)
                .orElseThrow(() -> new EmisionException(ReglasEmision.GENERACION_JOB_INEXISTENTE,
                        "No existe el job " + jobId));
        if (job.getEstado() != GeneracionJobEstado.PENDIENTE
                && job.getEstado() != GeneracionJobEstado.EN_PROCESO) {
            throw new EmisionException(ReglasEmision.GENERACION_JOB_NO_CANCELABLE,
                    "El job " + jobId + " está finalizado y no se puede cancelar");
        }
        jobService.marcarCancelado(jobId);
        return repository.findByJobId(jobId).orElse(job);
    }

    @Override
    public List<GeneracionJob> pendientes() {
        return repository.findPendientes();
    }

    private static void validarPadron(FiltroGeneracion filtro) {
        if (filtro == null || filtro.getLectivoId() == null || filtro.getFacultadId() == null
                || filtro.getGeograficaId() == null) {
            throw new EmisionException(ReglasEmision.GENERACION_FILTRO_INCOMPLETO,
                    "Debe indicar lectivo, facultad y sede");
        }
    }

    private static void validarOverride(boolean forzarDeuda, String motivoOverride) {
        if (forzarDeuda && (motivoOverride == null || motivoOverride.isBlank())) {
            throw new EmisionException(ReglasEmision.MOTIVO_OVERRIDE_REQUERIDO,
                    "Debe indicar el motivo para forzar la emisión con deuda excedida");
        }
    }

    /** El forzado de deuda exige `chequeras.emision.forzar`, igual que la emisión individual. */
    private void validarPermisoForzar(boolean forzarDeuda, Long usuarioId) {
        if (!forzarDeuda || usuarioId == null) {
            return;
        }
        if (!borradorEmisionService.claves(usuarioId).contains("chequeras.emision.forzar")) {
            throw new EmisionException(ReglasEmision.SIN_PERMISO_OVERRIDE,
                    "El usuario no tiene permiso para forzar la emisión con deuda excedida");
        }
    }

    private Map<String, PersonaKey> personas(List<BorradorChequera> borradores) {
        List<String> claves = borradores.stream()
                .map(borrador -> clavePersona(borrador.getPersonaId(), borrador.getDocumentoId()))
                .distinct()
                .toList();
        if (claves.isEmpty()) {
            return Map.of();
        }
        return personaKeyService.findAllByUnifiedIn(claves, List.of("apellido", "nombre")).stream()
                .collect(Collectors.toMap(PersonaKey::getUnified, Function.identity(), (primera, segunda) -> primera));
    }

    private Map<Integer, Integer> sedePorTipo() {
        Map<Integer, Integer> sedePorTipo = new LinkedHashMap<>();
        for (TipoChequera tipo : tipoChequeraService.findAll()) {
            if (tipo.getTipoChequeraId() != null && tipo.getGeograficaId() != null) {
                sedePorTipo.putIfAbsent(tipo.getTipoChequeraId(), tipo.getGeograficaId());
            }
        }
        return sedePorTipo;
    }

    private static List<Integer> tiposDeSede(Map<Integer, Integer> sedePorTipo, Integer geograficaId) {
        return sedePorTipo.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), geograficaId))
                .map(Map.Entry::getKey)
                .toList();
    }

    private static String clavePersona(java.math.BigDecimal personaId, Integer documentoId) {
        return (personaId == null ? "" : personaId.toPlainString()) + "." + (documentoId == null ? "" : documentoId);
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

    private static String recortar(String texto, int largo) {
        return texto == null || texto.length() <= largo ? texto : texto.substring(0, largo);
    }
}
