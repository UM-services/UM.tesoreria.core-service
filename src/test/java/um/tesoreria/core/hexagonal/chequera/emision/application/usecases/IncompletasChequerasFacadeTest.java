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
import um.tesoreria.core.hexagonal.chequera.emision.application.service.CompletarChequerasIncompletasService;
import um.tesoreria.core.hexagonal.chequera.emision.application.service.IncompletasJobService;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FiltroIncompletas;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJob;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobTarea;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasTareaEstado;
import um.tesoreria.core.hexagonal.chequera.emision.domain.ports.out.IncompletasJobRepository;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IncompletasChequerasFacadeTest {

    @Mock
    private IncompletasJobRepository repository;
    @Mock
    private CompletarChequerasIncompletasService orquestador;
    @Mock
    private IncompletasJobService jobService;
    @Mock
    private UsuarioService usuarioService;
    @Mock
    private TransactionOperations transaction;

    @InjectMocks
    private IncompletasChequerasFacade facade;

    @BeforeEach
    void setUp() {
        lenient().when(transaction.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
    }

    @Test
    void exigeLosCuatroFiltros() {
        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarBorradores(FiltroIncompletas.builder().lectivoId(38).build(), 1L));
        assertEquals("INCOMPLETAS_FILTRO_INCOMPLETO", exception.getCodigo());
    }

    @Test
    void noPermiteDosJobsDeBorradoresALaVez() {
        when(repository.findActivoByEtapa(IncompletasEtapa.BORRADORES))
                .thenReturn(Optional.of(IncompletasJob.builder().jobId(9L).build()));

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarBorradores(filtro(), 1L));
        assertEquals("INCOMPLETAS_JOB_ACTIVO", exception.getCodigo());
    }

    @Test
    void rechazaUnPadronVacio() {
        when(repository.findActivoByEtapa(IncompletasEtapa.BORRADORES)).thenReturn(Optional.empty());
        when(orquestador.seleccionar(any())).thenReturn(List.of());

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarBorradores(filtro(), 1L));
        assertEquals("INCOMPLETAS_SIN_TAREAS", exception.getCodigo());
    }

    @Test
    void creaElJobDeBorradoresConElPadronCompleto() {
        when(repository.findActivoByEtapa(IncompletasEtapa.BORRADORES)).thenReturn(Optional.empty());
        when(orquestador.seleccionar(any())).thenReturn(List.of(tarea(1L), tarea(2L)));
        when(repository.create(any(), anyList())).thenAnswer(invocation -> {
            IncompletasJob job = invocation.getArgument(0);
            job.setJobId(42L);
            return job;
        });

        IncompletasJob job = facade.iniciarBorradores(filtro(), 7L);

        assertEquals(42L, job.getJobId());
        assertEquals(IncompletasEtapa.BORRADORES, job.getEtapa());
        assertEquals(2, job.getTotalTareas());
        assertEquals(2, job.getTareasPendientes());
        assertEquals(null, job.getUsuarioLogin());
        assertTrue(job.getEstado() == IncompletasJobEstado.PENDIENTE);
    }

    @Test
    void noEmiteSiElJobDeBorradoresSigueEnCurso() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(IncompletasJob.builder()
                .jobId(5L)
                .etapa(IncompletasEtapa.BORRADORES)
                .estado(IncompletasJobEstado.EN_PROCESO)
                .build()));

        EmisionException exception = assertThrows(EmisionException.class,
                () -> facade.iniciarEmision(5L, null, 1L));
        assertEquals("INCOMPLETAS_JOB_ACTIVO", exception.getCodigo());
    }

    @Test
    void emiteSoloLosBorradoresSeleccionados() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(IncompletasJob.builder()
                .jobId(5L)
                .etapa(IncompletasEtapa.BORRADORES)
                .estado(IncompletasJobEstado.COMPLETADO)
                .lectivoId(38).facultadId(5).geograficaId(1).claseChequeraId(2)
                .build()));
        when(repository.findActivoByEtapa(IncompletasEtapa.EMISION)).thenReturn(Optional.empty());
        IncompletasJobTarea conBorrador1 = tarea(1L);
        conBorrador1.setBorradorId(11L);
        conBorrador1.setEstado(IncompletasTareaEstado.BORRADOR_CREADO);
        IncompletasJobTarea conBorrador2 = tarea(2L);
        conBorrador2.setBorradorId(22L);
        conBorrador2.setEstado(IncompletasTareaEstado.BORRADOR_REUTILIZADO);
        when(repository.findTareasByJobIdAndEstadoIn(eq(5L), anyList()))
                .thenReturn(List.of(conBorrador1, conBorrador2));
        when(repository.create(any(), anyList())).thenAnswer(invocation -> {
            IncompletasJob job = invocation.getArgument(0);
            job.setJobId(6L);
            return job;
        });

        IncompletasJob job = facade.iniciarEmision(5L, List.of(22L), 1L);

        assertEquals(6L, job.getJobId());
        assertEquals(IncompletasEtapa.EMISION, job.getEtapa());
        assertEquals(5L, job.getJobOrigenId());
        assertEquals(1, job.getTotalTareas());
    }

    @Test
    void noCancelaUnJobFinalizado() {
        when(repository.findByJobId(5L)).thenReturn(Optional.of(IncompletasJob.builder()
                .jobId(5L).estado(IncompletasJobEstado.COMPLETADO).build()));

        EmisionException exception = assertThrows(EmisionException.class, () -> facade.cancelar(5L));

        assertEquals("INCOMPLETAS_JOB_NO_CANCELABLE", exception.getCodigo());
    }

    @Test
    void informaJobInexistente() {
        when(repository.findByJobId(99L)).thenReturn(Optional.empty());

        EmisionException exception = assertThrows(EmisionException.class, () -> facade.consultar(99L));

        assertEquals("INCOMPLETAS_JOB_INEXISTENTE", exception.getCodigo());
        assertEquals("INCOMPLETAS_JOB_INEXISTENTE", exception.getCodigo());
    }

    private static FiltroIncompletas filtro() {
        return FiltroIncompletas.builder()
                .lectivoId(38).facultadId(5).geograficaId(1).claseChequeraId(2)
                .build();
    }

    private static IncompletasJobTarea tarea(Long chequeraId) {
        return IncompletasJobTarea.builder()
                .chequeraId(chequeraId)
                .personaId(new BigDecimal("49024730"))
                .documentoId(1)
                .facultadId(5)
                .tipoChequeraId(2)
                .chequeraSerieId(10L)
                .build();
    }
}
