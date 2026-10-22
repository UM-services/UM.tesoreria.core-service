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
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioHistorialPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioUsoPort;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeleteEjercicioUseCaseImplTest {
    @Mock EjercicioRepository repository;
    @Mock EjercicioUsoPort uso;
    @Mock EjercicioHistorialPort historial;
    DeleteEjercicioUseCaseImpl useCase;

    @BeforeEach
    void preparar() {
        Clock clock = Clock.fixed(Instant.parse("2026-10-21T15:00:00Z"), ZoneId.of("America/Argentina/Buenos_Aires"));
        useCase = new DeleteEjercicioUseCaseImpl(repository, uso, historial, TransactionOperations.withoutTransaction(), clock);
    }

    Ejercicio ultimo(int id, String inicio, String fin) {
        Ejercicio e = EjercicioSiguienteHelper.ejercicio(id, inicio, fin);
        when(repository.findLastWithLock()).thenReturn(Optional.of(e));
        return e;
    }

    void rechaza(int id, Codigo codigo) {
        assertThatThrownBy(() -> useCase.deleteEjercicio(id))
                .isInstanceOfSatisfying(EjercicioConflictoException.class, e -> assertThat(e.getCodigo()).isEqualTo(codigo));
        verify(repository, never()).deleteByEjercicioId(any());
        verifyNoInteractions(historial);
    }

    @Test
    void ultimoFuturoSinUsoSeBorraYDejaHistorial() {
        Ejercicio ultimo = ultimo(16, "2027-03-01", "2028-02-29");
        useCase.deleteEjercicio(16);
        verify(uso).hayMovimientos(LocalDate.parse("2027-03-01"), LocalDate.parse("2028-02-29"));
        var orden = inOrder(repository, historial);
        orden.verify(repository).deleteByEjercicioId(16);
        orden.verify(historial).registrarBaja(ultimo);
    }

    @Test
    void noEsElUltimo() {
        ultimo(16, "2027-03-01", "2028-02-29");
        when(repository.existsByEjercicioId(15)).thenReturn(true);
        rechaza(15, Codigo.NO_ES_ULTIMO);
    }

    @Test
    void ejercicioIniciadoEnElAnioActualNoEsFuturo() {
        ultimo(15, "2026-03-01", "2027-02-28");
        rechaza(15, Codigo.NO_FUTURO);
    }

    @Test
    void conMovimientosOAsientosEsEnUso() {
        ultimo(16, "2027-03-01", "2028-02-29");
        when(uso.hayMovimientos(any(), any())).thenReturn(true);
        rechaza(16, Codigo.EN_USO);
    }

    @Test
    void unaFkQueRechazaElDeleteSeTraduceAEnUsoYNoDejaHistorial() {
        ultimo(16, "2027-03-01", "2028-02-29");
        doThrow(new EjercicioConflictoException(Codigo.EN_USO, "fk")).when(repository).deleteByEjercicioId(16);
        assertThatThrownBy(() -> useCase.deleteEjercicio(16)).isInstanceOf(EjercicioConflictoException.class);
        verifyNoInteractions(historial);
    }

    @Test
    void inexistenteEsNoEncontrado() {
        ultimo(16, "2027-03-01", "2028-02-29");
        when(repository.existsByEjercicioId(99)).thenReturn(false);
        assertThatThrownBy(() -> useCase.deleteEjercicio(99)).isInstanceOf(EjercicioNoEncontradoException.class);
        verify(repository, never()).deleteByEjercicioId(any());
    }

    @Test
    void tablaVaciaEsNoEncontrado() {
        when(repository.findLastWithLock()).thenReturn(Optional.empty());
        assertThatThrownBy(() -> useCase.deleteEjercicio(1)).isInstanceOf(EjercicioNoEncontradoException.class);
        verify(repository, never()).deleteByEjercicioId(any());
    }

    @Test
    void ultimoDeUnAnioPasadoTampocoEsFuturo() {
        ultimo(10, "2025-03-01", "2026-02-28");
        rechaza(10, Codigo.NO_FUTURO);
    }

    @Test
    void unDeadlockSeReintentaYLaRepeticionVeElEstadoConfirmado() {
        var deadlock = new org.springframework.dao.CannotAcquireLockException("deadlock",
                new java.sql.SQLException("Deadlock found", "40001", 1213));
        when(repository.findLastWithLock()).thenThrow(deadlock)
                .thenReturn(Optional.of(EjercicioSiguienteHelper.ejercicio(17, "2028-03-01", "2029-02-28")));
        when(repository.existsByEjercicioId(16)).thenReturn(true);
        rechaza(16, Codigo.NO_ES_ULTIMO);
        verify(repository, times(2)).findLastWithLock();
    }

    @Test
    void siFallaElHistorialPropagaParaRevertir() {
        ultimo(16, "2027-03-01", "2028-02-29");
        doThrow(new IllegalStateException("historial")).when(historial).registrarBaja(any());
        assertThatThrownBy(() -> useCase.deleteEjercicio(16)).isInstanceOf(IllegalStateException.class);
    }
}
