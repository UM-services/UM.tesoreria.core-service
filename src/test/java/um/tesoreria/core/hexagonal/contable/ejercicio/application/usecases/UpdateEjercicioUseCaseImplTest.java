package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.transaction.support.TransactionOperations;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioConflictoException.Codigo;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioSolicitudInvalidaException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateEjercicioUseCaseImplTest {
    @Mock EjercicioRepository repository;
    @Mock EjercicioHistorialPort historial;
    UpdateEjercicioUseCaseImpl useCase;
    Ejercicio guardado = Ejercicio.builder().ejercicioId(15).nombre("Ejercicio Económico 2026")
            .fechaInicio(LocalDate.parse("2026-03-01")).fechaFinal(LocalDate.parse("2027-02-28")).bloqueado((byte) 0)
            .ordenContableResultado(7).ordenContableBienesUso(9).build();

    @BeforeEach
    void preparar() {
        useCase = new UpdateEjercicioUseCaseImpl(repository, historial, TransactionOperations.withoutTransaction());
    }

    void existe() {
        when(repository.findWithLockByEjercicioId(15)).thenReturn(Optional.of(guardado));
        lenient().when(repository.update(any())).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void soloBloqueadoConservaTodoLoDemasYRegistraHistorial() {
        existe();
        Ejercicio resultado = useCase.updateEjercicio(15, Ejercicio.builder().bloqueado((byte) 1).build());
        assertThat(resultado).isEqualTo(guardado.toBuilder().bloqueado((byte) 1).build());
        verify(historial).registrarEdicion(guardado, resultado);
    }

    @Test
    void soloUnaOrdenConservaBloqueadoYLaOtraOrden() {
        existe();
        Ejercicio resultado = useCase.updateEjercicio(15, Ejercicio.builder().ordenContableResultado(12).build());
        assertThat(resultado).isEqualTo(guardado.toBuilder().ordenContableResultado(12).build());
    }

    @Test
    void payloadCompletoDeVb6IgualAlGuardadoSeAceptaYSinCambioNoDejaHistorial() {
        existe();
        Ejercicio sinCambio = useCase.updateEjercicio(15, guardado.toBuilder().build());
        assertThat(sinCambio).isEqualTo(guardado);
        verify(repository, never()).update(any());
        verifyNoInteractions(historial);
    }

    @Test
    void payloadCompletoDeVb6ConBloqueadoDistintoCambiaSoloEseCampo() {
        existe();
        Ejercicio resultado = useCase.updateEjercicio(15, guardado.toBuilder().bloqueado((byte) 1).build());
        assertThat(resultado.getBloqueado()).isEqualTo((byte) 1);
        assertThat(resultado.getFechaInicio()).isEqualTo(guardado.getFechaInicio());
    }

    @Test
    void nombreOFechasDistintosSonCampoInmutable() {
        existe();
        for (Ejercicio cambios : new Ejercicio[]{
                Ejercicio.builder().nombre("Otro").build(),
                Ejercicio.builder().fechaInicio(LocalDate.parse("2026-04-01")).build(),
                Ejercicio.builder().fechaFinal(LocalDate.parse("2027-03-31")).build(),
                Ejercicio.builder().ejercicioId(16).build()}) {
            assertThatThrownBy(() -> useCase.updateEjercicio(15, cambios))
                    .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(Codigo.CAMPO_INMUTABLE));
        }
        verify(repository, never()).update(any());
        verifyNoInteractions(historial);
    }

    @Test
    void bloqueadoFueraDeCeroYUnoEsSolicitudInvalida() {
        assertThatThrownBy(() -> useCase.updateEjercicio(15, Ejercicio.builder().bloqueado((byte) 2).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        verifyNoInteractions(repository, historial);
    }

    @Test
    void ordenesFueraDeSmallintSonSolicitudInvalida() {
        assertThatThrownBy(() -> useCase.updateEjercicio(15, Ejercicio.builder().ordenContableResultado(40000).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        assertThatThrownBy(() -> useCase.updateEjercicio(15, Ejercicio.builder().ordenContableBienesUso(-5).build()))
                .isInstanceOf(EjercicioSolicitudInvalidaException.class);
        verifyNoInteractions(repository, historial);
    }

    @Test
    void nombreEnBlancoEIdEnCeroDeVb6SeTomanComoOmitidos() {
        existe();
        Ejercicio resultado = useCase.updateEjercicio(15, Ejercicio.builder().ejercicioId(0).nombre("").bloqueado((byte) 1).build());
        assertThat(resultado).isEqualTo(guardado.toBuilder().bloqueado((byte) 1).build());
    }

    @Test
    void inexistenteEsNoEncontrado() {
        when(repository.findWithLockByEjercicioId(99)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.updateEjercicio(99, Ejercicio.builder().bloqueado((byte) 1).build()))
                .isInstanceOf(EjercicioNoEncontradoException.class);
    }

    @Test
    void laLecturaConBloqueoEsLaUnicaLecturaDeLaFilaAntesDeEscribir() {
        existe();
        useCase.updateEjercicio(15, Ejercicio.builder().bloqueado((byte) 1).build());
        var orden = inOrder(repository);
        orden.verify(repository).findWithLockByEjercicioId(15);
        orden.verify(repository).update(any());
        verify(repository, never()).existsByEjercicioId(any());
    }
}
