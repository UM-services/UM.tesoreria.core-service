package um.tesoreria.core.hexagonal.chequera.emision.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionCallback;
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
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.PaginaBorradores;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.BorradorChequeraRepository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.GeneracionJobRepository;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.application.service.TipoChequeraService;
import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneracionBorradoresFacadeTest {

    @Mock
    private GeneracionJobRepository repository;
    @Mock
    private GeneracionBorradoresService orquestador;
    @Mock
    private GeneracionJobService jobService;
    @Mock
    private BorradorChequeraRepository borradorRepository;
    @Mock
    private BorradorEmisionService borradorEmisionService;
    @Mock
    private PersonaKeyService personaKeyService;
    @Mock
    private TipoChequeraService tipoChequeraService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private TransactionOperations transaction;

    @InjectMocks
    private GeneracionBorradoresFacade facade;

    @BeforeEach
    void setUp() {
        lenient().when(transaction.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Test
    void exigeLectivoFacultadYSede() {
        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarBorradores(FiltroGeneracion.builder().lectivoId(38).build(), 1L));
        assertEquals("GENERACION_FILTRO_INCOMPLETO", exception.getCodigo());
    }

    @Test
    void noPermiteDosJobsDeBorradoresALaVez() {
        when(repository.findActivoByEtapa(GeneracionEtapa.BORRADORES))
                .thenReturn(Optional.of(GeneracionJob.builder().jobId(9L).build()));

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarBorradores(filtro(), 1L));
        assertEquals("GENERACION_JOB_ACTIVO", exception.getCodigo());
    }

    @Test
    void rechazaUnPadronVacio() {
        when(repository.findActivoByEtapa(GeneracionEtapa.BORRADORES)).thenReturn(Optional.empty());
        when(orquestador.seleccionar(any())).thenReturn(List.of());

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarBorradores(filtro(), 1L));
        assertEquals("GENERACION_SIN_TAREAS", exception.getCodigo());
    }

    @Test
    void creaElJobDeBorradoresConElPadronCompleto() {
        when(repository.findActivoByEtapa(GeneracionEtapa.BORRADORES)).thenReturn(Optional.empty());
        when(orquestador.seleccionar(any())).thenReturn(List.of(tarea(1L), tarea(2L)));
        when(repository.create(any(), anyList())).thenAnswer(invocation -> {
            GeneracionJob job = invocation.getArgument(0);
            job.setJobId(42L);
            return job;
        });

        GeneracionJob job = facade.iniciarBorradores(filtro(), null);

        assertEquals(42L, job.getJobId());
        assertEquals(GeneracionEtapa.BORRADORES, job.getEtapa());
        assertEquals(2, job.getTotalTareas());
        assertEquals(2, job.getTareasPendientes());
        assertNull(job.getUsuarioLogin());
        assertEquals(GeneracionJobEstado.PENDIENTE, job.getEstado());
    }

    @Test
    void noEmiteSiElJobDeBorradoresSigueEnCurso() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(GeneracionJob.builder()
                .jobId(5L)
                .etapa(GeneracionEtapa.BORRADORES)
                .estado(GeneracionJobEstado.EN_PROCESO)
                .build()));

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarEmision(5L, null, false, null, 1L));
        assertEquals("GENERACION_JOB_ACTIVO", exception.getCodigo());
    }

    @Test
    void noEmiteSiElJobOrigenNoTieneBorradores() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(GeneracionJob.builder()
                .jobId(5L).etapa(GeneracionEtapa.BORRADORES).estado(GeneracionJobEstado.COMPLETADO).build()));
        when(repository.findActivoByEtapa(GeneracionEtapa.EMISION)).thenReturn(Optional.empty());
        when(repository.findTareasByJobIdAndEstadoIn(eq(5L), anyList())).thenReturn(List.of());

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarEmision(5L, null, false, null, 1L));
        assertEquals("GENERACION_SIN_BORRADORES", exception.getCodigo());
    }

    @Test
    void emiteLosBorradoresDelJobDeOrigen() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(GeneracionJob.builder()
                .jobId(5L).etapa(GeneracionEtapa.BORRADORES).estado(GeneracionJobEstado.COMPLETADO)
                .lectivoId(38).facultadId(5).geograficaId(1).build()));
        when(repository.findActivoByEtapa(GeneracionEtapa.EMISION)).thenReturn(Optional.empty());
        when(repository.findTareasByJobIdAndEstadoIn(eq(5L), anyList()))
                .thenReturn(List.of(tareaConBorrador(1L, 55L), tareaConBorrador(2L, 66L)));
        when(repository.create(any(), anyList())).thenAnswer(invocation -> {
            GeneracionJob job = invocation.getArgument(0);
            job.setJobId(43L);
            return job;
        });

        GeneracionJob job = facade.iniciarEmision(5L, List.of(66L), false, null, null);

        assertEquals(GeneracionEtapa.EMISION, job.getEtapa());
        assertEquals(5L, job.getJobOrigenId());
        assertEquals(1, job.getTotalTareas());
    }

    @Test
    void laEmisionDirectaArmaElPadronDeBorradoresDeLaSede() {
        when(tipoChequeraService.findAll()).thenReturn(List.of(
                TipoChequera.builder().tipoChequeraId(2).geograficaId(1).build(),
                TipoChequera.builder().tipoChequeraId(3).geograficaId(2).build()));
        when(repository.findActivoByEtapa(GeneracionEtapa.EMISION)).thenReturn(Optional.empty());
        when(borradorRepository.findAllByLectivoIdAndFacultadIdAndTipoChequeraIdInAndEstado(
                eq(38), eq(5), eq(List.of(2)), eq(EstadoBorrador.ABIERTO), anyInt(), anyInt()))
                .thenReturn(new PaginaBorradores(List.of(BorradorChequera.builder()
                        .borradorId(55L).personaId(new BigDecimal("1234567")).documentoId(1)
                        .facultadId(5).tipoChequeraId(2).chequeraSerieId(10L).build()), 1));
        when(personaKeyService.findAllByUnifiedIn(anyList(), anyList())).thenReturn(List.of());
        when(repository.create(any(), anyList())).thenAnswer(invocation -> {
            GeneracionJob job = invocation.getArgument(0);
            job.setJobId(44L);
            return job;
        });

        GeneracionJob job = facade.iniciarEmisionDirecta(filtro(), null);

        assertEquals(GeneracionEtapa.EMISION, job.getEtapa());
        assertNull(job.getJobOrigenId());
        assertEquals(1, job.getTotalTareas());
        verify(repository).create(any(), anyList());
    }

    @Test
    void exigeMotivoCuandoSeFuerzaLaDeuda() {
        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarEmisionDirecta(FiltroGeneracion.builder()
                        .lectivoId(38).facultadId(5).geograficaId(1).forzarDeuda(true).build(), 1L));
        assertEquals("MOTIVO_OVERRIDE_REQUERIDO", exception.getCodigo());
    }

    @Test
    void rechazaElForzadoDeDeudaSinPermiso() {
        when(borradorEmisionService.claves(1L)).thenReturn(Set.of("chequeras.generacion.emision"));

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarEmisionDirecta(FiltroGeneracion.builder()
                        .lectivoId(38).facultadId(5).geograficaId(1)
                        .forzarDeuda(true).motivoOverride("Autorización").build(), 1L));
        assertEquals("SIN_PERMISO_OVERRIDE", exception.getCodigo());
    }

    @Test
    void noCancelaUnJobFinalizado() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(GeneracionJob.builder()
                .jobId(5L).estado(GeneracionJobEstado.COMPLETADO).build()));

        EmisionException exception = assertThrows(EmisionException.class, () -> facade.cancelar(5L));
        assertEquals("GENERACION_JOB_NO_CANCELABLE", exception.getCodigo());
        verify(jobService, never()).marcarCancelado(any());
    }

    private static FiltroGeneracion filtro() {
        return FiltroGeneracion.builder().lectivoId(38).facultadId(5).geograficaId(1).soloNoEnviadas(true).build();
    }

    private static GeneracionJobTarea tarea(Long chequeraId) {
        return GeneracionJobTarea.builder().chequeraId(chequeraId)
                .estado(GeneracionTareaEstado.PENDIENTE).build();
    }

    private static GeneracionJobTarea tareaConBorrador(Long chequeraId, Long borradorId) {
        return GeneracionJobTarea.builder().chequeraId(chequeraId).borradorId(borradorId)
                .estado(GeneracionTareaEstado.BORRADOR_CREADO).build();
    }
}
