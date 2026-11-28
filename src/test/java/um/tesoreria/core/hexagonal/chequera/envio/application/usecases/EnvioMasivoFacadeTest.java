package um.tesoreria.core.hexagonal.chequera.envio.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.chequera.envio.application.exception.EnvioException;
import um.tesoreria.core.hexagonal.chequera.envio.application.service.EnvioJobService;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.ChequeraEnvioAlta;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJob;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobTarea;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioOperacion;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.FiltroEnvio;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.ChequeraEnvioAltaReader;
import um.tesoreria.core.hexagonal.chequera.envio.domain.ports.out.EnvioJobRepository;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.service.UsuarioService;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnvioMasivoFacadeTest {

    @Mock
    private EnvioJobRepository repository;
    @Mock
    private ChequeraEnvioAltaReader reader;
    @Mock
    private EnvioJobService jobService;
    @Mock
    private UsuarioService usuarioService;

    private EnvioMasivoFacade facade;

    @BeforeEach
    void setUp() {
        facade = new EnvioMasivoFacade(repository, reader, jobService, usuarioService,
                TransactionOperations.withoutTransaction());
    }

    private static FiltroEnvio filtro(boolean soloNoEnviadas) {
        return FiltroEnvio.builder()
                .operacion(EnvioOperacion.MAILING)
                .lectivoId(37).facultadId(2).geograficaId(1)
                .soloNoEnviadas(soloNoEnviadas)
                .build();
    }

    private static ChequeraEnvioAlta alta(long chequeraId, boolean enviado) {
        return new ChequeraEnvioAlta(chequeraId, 2, 520, 446L, 1, new BigDecimal("1001"), 1, 37, 1,
                "ABALLAY MURUA", "Luna Martina", "Matrícula y Arancel", "Ciclo Completo",
                "a@b.com", null, true, false, enviado);
    }

    @Test
    void excluyeLasYaEnviadasCuandoElFiltroEstaActivo() {
        when(reader.findAllByLectivoIdAndFacultadIdAndGeograficaId(37, 2, 1))
                .thenReturn(List.of(alta(1L, true), alta(2L, false), alta(3L, true)));
        when(repository.create(any(), anyList())).thenAnswer(inv -> inv.getArgument(0));

        facade.iniciar(filtro(true), 9L);

        ArgumentCaptor<List<EnvioJobTarea>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).create(any(), captor.capture());
        assertThat(captor.getValue()).extracting(EnvioJobTarea::getChequeraId).containsExactly(2L);
    }

    @Test
    void incluyeLasEnviadasSiElFiltroEstaApagado() {
        when(reader.findAllByLectivoIdAndFacultadIdAndGeograficaId(37, 2, 1))
                .thenReturn(List.of(alta(1L, true), alta(2L, false)));
        when(repository.create(any(), anyList())).thenAnswer(inv -> inv.getArgument(0));

        facade.iniciar(filtro(false), null);

        ArgumentCaptor<List<EnvioJobTarea>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).create(any(), captor.capture());
        assertThat(captor.getValue()).hasSize(2);
    }

    @Test
    void fallaSiNoHayFilasParaLosFiltros() {
        when(reader.findAllByLectivoIdAndFacultadIdAndGeograficaId(37, 2, 1)).thenReturn(List.of());

        assertThatThrownBy(() -> facade.iniciar(filtro(false), null))
                .isInstanceOf(EnvioException.class)
                .extracting("codigo").isEqualTo("ENVIO_SIN_TAREAS");
        verify(repository, never()).create(any(), anyList());
    }

    @Test
    void fallaSiYaHayUnJobEnCurso() {
        when(repository.findActivo()).thenReturn(Optional.of(EnvioJob.builder()
                .jobId(5L).estado(EnvioJobEstado.EN_PROCESO).build()));

        assertThatThrownBy(() -> facade.iniciar(filtro(false), null))
                .isInstanceOf(EnvioException.class)
                .extracting("codigo").isEqualTo("ENVIO_JOB_ACTIVO");
        verify(reader, never()).findAllByLectivoIdAndFacultadIdAndGeograficaId(any(), any(), any());
    }

    @Test
    void validaLosFiltrosObligatorios() {
        var incompleto = FiltroEnvio.builder().operacion(EnvioOperacion.MAILING).lectivoId(37).build();

        assertThatThrownBy(() -> facade.iniciar(incompleto, null))
                .isInstanceOf(EnvioException.class)
                .extracting("codigo").isEqualTo("ENVIO_FILTRO_INCOMPLETO");
    }

    @Test
    void reintentarSoloFallidasReactivaLosEstadosCorrespondientes() {
        when(repository.findByJobId(1L)).thenReturn(Optional.of(EnvioJob.builder()
                .jobId(1L).estado(EnvioJobEstado.COMPLETADO).build()));
        when(repository.findActivo()).thenReturn(Optional.empty());
        when(repository.reactivarTareas(eq(1L), anyList())).thenReturn(2);
        when(repository.countTareasByJobIdAndEstado(eq(1L), any())).thenReturn(2L);
        when(repository.update(any())).thenAnswer(inv -> inv.getArgument(0));

        var job = facade.reintentar(1L, true);

        assertThat(job.getEstado()).isEqualTo(EnvioJobEstado.PENDIENTE);
        assertThat(job.getTareasPendientes()).isEqualTo(2);
    }

    @Test
    void noReintentaUnJobEnCurso() {
        when(repository.findByJobId(1L)).thenReturn(Optional.of(EnvioJob.builder()
                .jobId(1L).estado(EnvioJobEstado.EN_PROCESO).build()));

        assertThatThrownBy(() -> facade.reintentar(1L, true))
                .isInstanceOf(EnvioException.class)
                .extracting("codigo").isEqualTo("ENVIO_JOB_ACTIVO");
    }
}
